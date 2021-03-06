
export M2_HOME=/opt/maven/apache-maven-2.2.1
PATH=$M2_HOME/bin:$PATH

mvn clean install -Dmaven.test.skip=true
mvn dependency:sources
mvn dependency:resolve -Dclassifier=javadoc


# I am using the mybatis-generator maven plugin. So, I need always a database (it would be better to use the Eclipse plugin)
mysql -uroot -proot -e "CREATE DATABASE mybatis_example DEFAULT CHARACTER SET utf8mb4"

mysql -uroot -proot -e "USE mybatis_example; CREATE TABLE ad (id SERIAL, company_id BIGINT, company_categ_id BIGINT, ad_gps BLOB, ad_mobile_image varchar(255), created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4, COLLATE=utf8mb4_unicode_ci"

mysql -uroot -proot -e "USE mybatis_example; CREATE TABLE ad_description (id SERIAL, laguage_id BIGINT NOT NULL, ad_id BIGINT(20) UNSIGNED NOT NULL, ad_name VARCHAR(255) NOT NULL, ad_description LONGTEXT, ad_mobile_text VARCHAR(500) NOT NULL, ad_link VARCHAR(3000) NOT NULL, PRIMARY KEY (id), INDEX(ad_id), FOREIGN KEY (ad_id) REFERENCES ad (id) ON DELETE CASCADE) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4, COLLATE=utf8mb4_unicode_ci"

# Run mvn clean install (mybatis-generator plugin creates autogenerated code from tables)
mvn clean install

# Drop database (we want to check liquibase is working with Spring)
mysql -uroot -proot -A -e "drop database mybatis_example"

# Liquibase requires (AFAIK) of an existing schema
mysql -uroot -proot -e "CREATE DATABASE mybatis_example DEFAULT CHARACTER SET utf8mb4"

# Run TestMain.java :)


# Useful commands for liquibase that I used in order to create the changelog files from an existing schema.
# Changelog from DDL. Creates DDL changelog from current database (if schema was previously created without liquibase as above)
/opt/liquibase/liquibase --driver=com.mysql.jdbc.Driver --classpath=$HOME/.m2/repository/mysql/mysql-connector-java/5.1.9/mysql-connector-java-5.1.9.jar --logLevel=debug --changeLogFile=src/main/resources/liquibase/ddlChangelog.xml --url="jdbc:mysql://localhost/mybatis_example" --username=root --password=root generateChangeLog 

# Changelog for DML. Creates DML changelog from current database (if there are data)
/opt/liquibase/liquibase --driver=com.mysql.jdbc.Driver --classpath=$HOME/.m2/repository/mysql/mysql-connector-java/5.1.9/mysql-connector-java-5.1.9.jar --logLevel=debug --changeLogFile=src/main/resources/liquibase/dmlChangelog.xml --url="jdbc:mysql://localhost/mybatis_example" --username=root --password=root --diffTypes="data" generateChangeLog
