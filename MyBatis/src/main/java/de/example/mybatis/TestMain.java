package de.example.mybatis;

import java.io.IOException;
import java.util.Date;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import de.example.mybatis.model.Ad;
import de.example.mybatis.repository.mapper.AdMapper;

//import de.example.ibatis.demo.dao.UserDao;
//import de.example.ibatis.demo.dao.UserDaoIbatis;
//import de.example.ibatis.dto.UserTEO;

public class TestMain {

    public static void main(final String[] args) throws IOException {
        //Initialize dao
        //UserDao manager = new UserDaoIbatis();

        //		File file = new File("sql-maps-config.xml");
        //		FileInputStream fileInputStream = new FileInputStream(file);
        // AL FINAL NO NECESITABA TODO ESTO DE FILECHANNEL Y BYTEBUFFERS xD se me fue la pinza :(
        // al menos mira lo del FileChannel.size() porque es interesante recordarlo
        // y saber que si el archivo puede cambiar el metodo FileChannel.size() no te vale :(
        // y tendrias que usar algo como lo que hiciste en AxisC3 driver :)
        //		FileChannel fileChannel = fileInputStream.getChannel();

        // FileChannel.size() internamente usa fstat64 recuperando st_size
        // asi puede saber el tamaño del archivo antes de leerlo. Esto puede
        // ser un problema si el archivo puede cambiar por alguna razon desconocida
        // mientras lo estoy leyendo.
        // ver: FileChannelImpl.c (la implementacion nativa de FileChannel)
        // otra solucion es lo que hice para AxisC3, un array de byes mutable
        // que se incrementa tanto como haga falta hasta que haya leido todos los bytes
        // Por tanto si a priori no puedo saber el tamaño del archivo a leer deberia
        // usar un array de bytes mutables cuyo tamaño puede incrementarse si hace falta
        // como hice con el AxisC3 :)

        //fileInputStream.read(new byte[(int)fileChannel.size()]); <----- puedo leer tambien asi pero me apetece usar byte buffers
        // de java.nio :)


        //joer.size devuelve long!!!! hago casta int pierdo datos :(  ¿solucion?
        //		ByteBuffer gus = ByteBuffer.allocate((int)fileChannel.size());
        //		int result = 0;
        //		while (result != -1) {
        //			result = fileChannel.read(gus);
        //		}
        //		TestMain.class.getResourceAsStream("sql-maps-config.xml");
        //		Reader reader = Resources.getResourceAsReader("sql-maps-config.xml");

        // Desde el Javadoc de org.xml.sax.InputSource:
        //		 <p>The SAX parser will use the InputSource object to determine how
        //		 to read XML input.  If there is a character stream available, the
        //		 parser will read that stream directly, disregarding any text
        //		 encoding declaration found in that stream.
        //		 If there is no character stream, but there is
        //		 a byte stream, the parser will use that byte stream, using the
        //		 encoding specified in the InputSource or else (if no encoding is
        //		 specified) autodetecting the character encoding using an algorithm
        //		 such as the one in the XML specification.  If neither a character
        //		 stream nor a
        //		 byte stream is available, the parser will attempt to open a URI
        //		 connection to the resource identified by the system
        //		 identifier.</p>

        // LUEGO SI LE PASO UN FileInputStream/InputStream (no es un character stream) y no especifico el encoding
        // se deberia autodetectar el encoding usando la especificacion XML (leyendo
        // la cabecera del archivo xml) GUAY!!!! JUSTO LO QUE ME GUSTA :)
        //		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(fileInputStream);

        // JOER NO HACIA FALTA TANTO LIO PARA CONSEGUIR UN INPUTSTREAM. DE HECHO LA MEJOR
        // FORMA ES HACERLO ASI SIEMPRE (con el getResourceAsStream) ASÍ NO DEPENDES
        // DE PATHS ABSOLUTIOS NI NADA, TE SIRVE PARA .jars, O DIRECTORIOS FISICOS, LO QUE SEA QUE ESTE
        // EN EL CLASSPATH:
        final SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
        .build(/**TestMain.class.getResourceAsStream("sql-maps-config.xml")**/
                Resources.getResourceAsStream("mybatis-sql-maps-config.xml"));
        final SqlSession session = sqlSessionFactory.openSession();
        // EN MYBATIS HAY UNA FORMA MEJOR QUE ESTA
        //		try {
        //		Ad ad = session.selectOne("de.example.mybatis.model.Ad.selectByPrimaryKey", 101);
        //		} finally {
        //			session.close();
        //		}
        // ESTA FORMA ES MUCHO MEJOR :)
        try {
            final AdMapper mapper = session.getMapper(AdMapper.class);
            final Ad adprueba = new Ad();
            adprueba.setCompanyCategId(200L);
            adprueba.setCreatedAt(new Date());
            adprueba.setCompanyId(2L);
            adprueba.setUpdatedAt(new Date());
            mapper.insert(adprueba);
            //			Ad ad = mapper.selectByPrimaryKey(1000L);
        } finally {
            session.close();
        }

        // De esa forma consigo que el encoding dependa del valor puesto en la cabecera del xml :)

        //		SqlSession session = sqlSessionFactory.openSession();
        //		try {
        //		  Blog blog = session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
        //		} finally {
        //		  session.close();
        //		}
        //
        //		SqlSession session = sqlSessionFactory.openSession();
        //		try {
        //		  BlogMapper mapper = session.getMapper(BlogMapper.class);
        //		  Blog blog = mapper.selectBlog(101);
        //		} finally {
        //		  session.close();
        //		}
        //		SqlMapClient sqlmapClient = SqlMapClientBuilder.buildSqlMapClient (reader);
        //
        //		//Create a new user to persist
        //		UserTEO user = new UserTEO();
        //		user.setId(1);
        //		user.setName("Demo User");
        //		user.setPassword("password");
        //		user.setEmail("demo-user@howtodoinjava.com");
        //		user.setStatus(1);
        //
        //		//Add the user
        //		manager.addUser(user,sqlmapClient);
        //
        //		//Fetch the user detail
        //		UserTEO createdUser = manager.getUserById(4, sqlmapClient);
        //		System.out.println(createdUser.getEmail());
        //
        //		//Lets delete the user
        //		manager.deleteUserById(1, sqlmapClient);
        //		try {
        //			Thread.sleep(100000);
        //		} catch (InterruptedException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //		}
    }

}
