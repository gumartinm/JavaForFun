package de.sql.tests;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class RawSpringJDBCWithPoolExample {
    private static final Logger logger = LoggerFactory.getLogger(RawSpringJDBCWithPoolExample.class);


    public static void main(final String[] args) throws PropertyVetoException {
        // Just for fun, programmatic configuration.
        final DataSource dataSource = getDataSource();


        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("AD_ID", 1);


        // 3. Using Spring JdbcTemplate
        final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("SELECT * FROM AD");


        // 4. Using SimpleJdbcTemplate
        final SimpleJdbcOperations simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
        final int deprecatedResult = simpleJdbcTemplate.queryForInt("SELECT * FROM AD", parameters);
        logger.info("Deprecated result: " + deprecatedResult);


        // 5. Using NamedParameterJdbcTemplate
        final NamedParameterJdbcOperations namedParameterJdbcOperations = new NamedParameterJdbcTemplate(dataSource);
        final int namedResult = namedParameterJdbcOperations.queryForInt("SELECT * FROM AD", parameters);
        logger.info("Named result: " + namedResult);


        // 6. Using Spring SimpleJdbcInsert
        final SimpleJdbcInsertOperations simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
        simpleJdbcInsert.withTableName("ad");
        simpleJdbcInsert.execute(parameters);

        // Now we close the whole pool :)
        ((ComboPooledDataSource)dataSource).close();
    }

    /**
     * Just for fun, programmatic configuration.
     * @return
     * @throws PropertyVetoException
     */
    private static DataSource getDataSource() throws PropertyVetoException {
        final ComboPooledDataSource pool = new ComboPooledDataSource();

        pool.setUser("root");
        pool.setPassword("");
        // We are going to use the JDBC driver
        pool.setDriverClass("com.mysql.jdbc.Driver");
        pool.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/n2a?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF-8");
        pool.setInitialPoolSize(5);
        pool.setMaxPoolSize(35);
        pool.setMinPoolSize(10);
        pool.setAcquireIncrement(1);
        pool.setAcquireRetryAttempts(5);
        pool.setAcquireRetryDelay(1000);
        pool.setAutomaticTestTable("con_test");
        pool.setCheckoutTimeout(5000);

        return pool;
    }
}
