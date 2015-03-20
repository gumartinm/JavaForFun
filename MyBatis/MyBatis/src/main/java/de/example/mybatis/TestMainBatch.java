package de.example.mybatis;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import de.example.mybatis.model.Ad;
import de.example.mybatis.model.AdCriteria;
import de.example.mybatis.repository.mapper.AdMapper;


public class TestMainBatch {
    private static final Logger logger = Logger.getLogger(TestMainBatch.class);

    public static void main(final String[] args) throws IOException {

        // From org.xml.sax.InputSource Javadoc:
        // The SAX parser will use the InputSource object to determine how to
        // read XML input. If there is a character stream available, the parser
        // will read that stream directly, disregarding any text encoding
        // declaration found in that stream. If there is no character stream,
        // but there is a byte stream, the parser will use that byte stream,
        // using the encoding specified in the InputSource or else (if no
        // encoding is specified) autodetecting the character encoding using an
        // algorithm such as the one in the XML specification. If neither a
        // character stream nor a byte stream is available, the parser will
        // attempt to open a URI connection to the resource identified by the
        // system identifier.

        // Then if we use an InputStream (it is not a character stream) and
        // we do not specify the encoding, the encoding should be autodetected
        // reading the XML header. :) That is what I want. :)



        // Scope and Lifecycle
        //
        // 1. SqlSessionFactoryBuilder:
        // This class can be instantiated, used and thrown away. There is no
        // need to keep it around once you've created your SqlSessionFactory.
        //
        // 2. SqlSessionFactory:
        // Once created, the SqlSessionFactory should exist for the duration of
        // your application execution.
        //
        // 3. SqlSession:
        // Each thread should have its own instance of SqlSession. Instances of
        // SqlSession are not to be shared and are not thread safe. Therefore
        // the best scope is request or method scope. You should always ensure
        // that it's closed within a finally block.
        //
        // 4. Mapper Instances:
        // Mappers are interfaces that you create to bind to your mapped
        // statements. Instances of the mapper interfaces are acquired from the
        // SqlSession. They do not need to be closed explicitly.

        final SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
        .build(/**TestMain.class.getResourceAsStream("sql-maps-config.xml")**/
                Resources.getResourceAsStream("mybatis-sql-maps-config.xml"), "mybatisexample");

        // The default openSession() method that takes no parameters will create
        // a SqlSession with the following characteristics:
        //
        // * A transaction scope will be started (i.e. NOT auto-commit).
        // * A Connection object will be acquired from the DataSource instance
        // configured by the active environment.
        // * The transaction isolation level will be the default used by the
        // driver or data source.
        // * No PreparedStatements will be reused, and no updates will be
        // batched.

        // MyBatis uses two caches: a local cache and a second level cache.
        //
        // Each time a new session is created MyBatis creates a local cache and
        // attaches it to the session. Any query executed within the session
        // will be stored in the local cache so further executions of the same
        // query with the same input parameters will not hit the database. The
        // local cache is cleared upon update, commit, rollback and close.
        //
        // By default local cache data is used for the whole session duration.
        // This cache is needed to resolve circular references and to speed up
        // repeated nested queries, so it can never be completely disabled but
        // you can configure the local cache to be used just for the duration of
        // an statement execution by setting localCacheScope=STATEMENT.
        //
        // Note that when the localCacheScope is set to SESSION, MyBatis returns
        // references to the same objects which are stored in the local cache.
        // Any modification of returned object (lists etc.) influences the local
        // cache contents and subsequently the values which are returned from
        // the cache in the lifetime of the session. Therefore, as best
        // practice, do not to modify the objects returned by MyBatis.
        /** 
         * OVERRIDE THE DECLARED EXECUTOR IN mybatis-sql-maps-config.xml
         *  BY DEFAULT autocommit = false (what means autocommit=0 until session.close is called)
         */
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);

        try {
            final AdMapper adMapper = session.getMapper(AdMapper.class);
            final Ad adTest = new Ad();
            adTest.setAdMobileImage("mobileImage.jpg");
            adTest.setCompanyCategId(200L);
            adTest.setCreatedAt(new Date());
            adTest.setCompanyId(2L);
            adTest.setUpdatedAt(new Date());

            // This first insert took ages because I was dropping IPV6 connections.
            // That is because during the first socket connection, the JVM
            // tries to find out if IPV6 is available by means of opening a random
            // AF_INET6 POSIX socket.
            /** 
             * WITH BATCH OPERATIONS, INSERT WILL NOT BE PEFORMED UNTIL CALLING COMMIT OR FLUSHSTATEMENTS
             * BUT set autocommit=0 WILL BE PEFORMED JUST IN THIS VERY MOMENT EVEN IF WE ARE NOT
             * YET SENDING DATA!!!!! So, it is as always but without sending data.
             */
            adMapper.insert(adTest);
            adMapper.insert(adTest);
            /**
             * BY MEANS OF THIS METHOD WE CAN SEND THE BATCHED DATA TO DATA BASE AND
             * RETRIEVE THE RESULTS OF THE BATCH OPERATIONS. OTHERWISE WE HAVE TO WAIT
             * UNTIL session.commit AND WE LOOSE THE RESULTS.
             */
            session.flushStatements();
            
            /** 
             *  IF WE DIDN'T USE THE flushStatements METHOD, IN THIS VERY MOMENT BATCH DATA WOULD BE SENT TO SERVER
             *  (BUT BECAUSE WE USED THE flushStatements METHOD NO DATA WILL BE SENT)
             *  JUST THE commit COMMAND IS SENT RIGHT NOW!!!
             */
            session.commit();

            final List<Ad> adLists = adMapper.selectByExample(null);
            for (final Ad ad : adLists) {
                logger.info("Ad id: " + ad.getId());
                if (ad.getAdGps() != null) {
                    logger.info("Ad GPS: " + new String(ad.getAdGps(), "UTF-8"));
                }
                logger.info("Ad mobileImage: " + ad.getAdMobileImage());
                logger.info("Ad companyCategId: " + ad.getCompanyCategId());
                logger.info("Ad companyId: " + ad.getCompanyId());
                logger.info("Ad createdAt: " + ad.getCreatedAt());
                logger.info("Ad updatedAt: " + ad.getUpdatedAt());
                logger.info("\n");
            }
        } finally {
            // Besides this will restore the auto-commit value.
        	/** 
             *  JUST IN THIS VERY MOMENT set autocommit=1 IS SENT!!!!!
             *  
             *  This is what @Transactional (Spring annotation) do when
             *  returning from an annotated transactional method.
             */
            session.close();
        }

        /** 
         * OVERRIDE THE DECLARED EXECUTOR IN mybatis-sql-maps-config.xml
         *  BY DEFAULT autocommit = false (what means autocommit=0 until session.close is called)
         */
        session = sqlSessionFactory.openSession(ExecutorType.BATCH);

        try {
            logger.info("Last insert");
            final AdMapper adMapper = session.getMapper(AdMapper.class);
            final Ad adTest = new Ad();
            adTest.setAdMobileImage("mobileImage.jpg");
            adTest.setCompanyCategId(200L);
            adTest.setCreatedAt(new Date());
            adTest.setCompanyId(2L);
            adTest.setUpdatedAt(new Date());
            
            /** 
             * WITH BATCH OPERATIONS, INSERT WILL NOT BE PEFORMED UNTIL CALLING COMMIT
             * (IF WE DON'T USE THE flushStatements METHOD)
             * BUT set autocommit=0 WILL BE PEFORMED JUST IN THIS VERY MOMENT EVEN IF WE ARE NOT
             * YET SENDING DATA!!!!! So, it is as always but without sending data.
             */
            adMapper.insert(adTest);
            
            /** 
             *  JUST IN THIS VERY MOMENT DATA ARE SENT TO SERVER, BUT NOT BEFORE!!!!
             *  (BECAUSE WE DID NOT USE THE flushStatements METHOD)
             *  THE commit COMMAND IS SENT RIGHT NOW!!!!!
             */
            session.commit();

        } finally {
            // Besides this will restore the auto-commit value.
        	/** 
             *  JUST IN THIS VERY MOMENT set autocommit=1 IS SENT!!!!!
             *  
             *  This is what @Transactional (Spring annotation) do when
             *  returning from an annotated transactional method.
             */
            session.close();
        }

        session = sqlSessionFactory.openSession();

        try {
            logger.info("Using criteria");

            final AdCriteria adCriteria = new AdCriteria();

            adCriteria.or().andAdMobileImageEqualTo("mobileImage.jpg")
                    .andCreatedAtNotEqualTo(new Date());

            adCriteria.or().andAdMobileImageNotEqualTo("noMobileImage.jpg")
                    .andAdMobileImageIsNotNull();

            // where (ad_mobile_image = "mobileImage.jpg" and created_at <> Now())
            // or (ad_mobile_image <> "noMobileImage.jpg" and ad_mobile_image is not null)

            final AdMapper adMapper = session.getMapper(AdMapper.class);
            final List<Ad> adLists = adMapper.selectByExampleWithBLOBs(adCriteria);
            for (final Ad ad : adLists) {
                logger.info("Ad id: " + ad.getId());
                if (ad.getAdGps() != null) {
                    logger.info("Ad GPS: " + new String(ad.getAdGps(), "UTF-8"));
                }
                logger.info("Ad mobileImage: " + ad.getAdMobileImage());
                logger.info("Ad companyCategId: " + ad.getCompanyCategId());
                logger.info("Ad companyId: " + ad.getCompanyId());
                logger.info("Ad createdAt: " + ad.getCreatedAt());
                logger.info("Ad updatedAt: " + ad.getUpdatedAt());
                logger.info("\n");
            }
        } finally {
            // Besides this will restore the auto-commit value.
        	/** 
             *  JUST IN THIS VERY MOMENT set autocommit=1 IS SENT!!!!!
             *  
             *  This is what @Transactional (Spring annotation) do when
             *  returning from an annotated transactional method.
             */
            session.close();
        }
    }

}
