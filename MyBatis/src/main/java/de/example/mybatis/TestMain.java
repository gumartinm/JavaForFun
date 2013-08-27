package de.example.mybatis;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import de.example.mybatis.model.Ad;
import de.example.mybatis.repository.mapper.AdMapper;


public class TestMain {
    private static final Logger logger = Logger.getLogger(TestMain.class);

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
        final SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
        .build(/**TestMain.class.getResourceAsStream("sql-maps-config.xml")**/
                Resources.getResourceAsStream("mybatis-sql-maps-config.xml"), "mybatisexample");

        SqlSession session = sqlSessionFactory.openSession();

        try {
            final AdMapper adMapper = session.getMapper(AdMapper.class);
            final Ad adTest = new Ad();
            adTest.setAdMobileImage("mobileImage.jpg");
            adTest.setCompanyCategId(200L);
            adTest.setCreatedAt(new Date());
            adTest.setCompanyId(2L);
            adTest.setUpdatedAt(new Date());
            adMapper.insert(adTest);
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
            session.close();
        }

        session = sqlSessionFactory.openSession();

        try {
            logger.info("Last insert");
            final AdMapper adMapper = session.getMapper(AdMapper.class);
            final Ad adTest = new Ad();
            adTest.setAdMobileImage("mobileImage.jpg");
            adTest.setCompanyCategId(200L);
            adTest.setCreatedAt(new Date());
            adTest.setCompanyId(2L);
            adTest.setUpdatedAt(new Date());
            adMapper.insert(adTest);
            session.commit();

        } finally {
            session.close();
        }
    }

}
