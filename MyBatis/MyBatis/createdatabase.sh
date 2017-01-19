#!/bin/bash
mysql -uroot -proot -e "CREATE DATABASE mybatis_example DEFAULT CHARACTER SET utf8mb4"

mysql -uroot -proot -e "USE mybatis_example; CREATE TABLE ad (id SERIAL, company_id BIGINT, company_categ_id BIGINT, ad_gps BLOB, ad_mobile_image varchar(255), created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4, COLLATE=utf8mb4_unicode_ci"

# Por alguna extraña razon no pude poner el id de esta tabla como serial y no tiene ahora este id autoincrement
# Daba un error raro diciendo que no podia haber 2 claves con autoincrement en la misma tabla. Pero sí se puede...
# debo estar haciendo algo mal... :(
mysql -uroot -proot -e "USE mybatis_example; CREATE TABLE ad_description (id BIGINT(20) UNSIGNED NOT NULL, laguage_id BIGINT NOT NULL, ad_id SERIAL NOT NULL, ad_name VARCHAR(255) NOT NULL, ad_description LONGTEXT, ad_mobile_text VARCHAR(500) NOT NULL, ad_link VARCHAR(3000) NOT NULL, PRIMARY KEY (id), INDEX(ad_id), FOREIGN KEY (ad_id) REFERENCES ad (id) ON DELETE CASCADE) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4, COLLATE=utf8mb4_unicode_ci"
