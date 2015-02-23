package de.example.mybatis.spring.service;

import org.apache.ibatis.jdbc.SQL;

/**
 * Taken from: http://mybatis.github.io/mybatis-3/statement-builders.html
 *
 */
public class ExampleBuilderService {

	// Builder / Fluent style
	public String insertAdSql() {
		return new SQL()
		.INSERT_INTO("ad_description")
	    .VALUES("ad_name, ad_description", "${adName}, ${adDescription}")
	    .VALUES("ad_mobile_text", "${adMobileText}")
	    .toString();
	}

	// With conditionals (note the final parameters, required for the anonymous inner class to access them)
	public String selectAdLike(final String id, final String adName, final String lastName) {
		return new SQL() {
			{ // New block!!!!
				SELECT("AD.id, AD.laguage_id, AD.ad_name, AD.ad_description, AD.ad_mobile_text");
				FROM("ad_description AD");
				if (id != null) {
					WHERE("AD.id like ${id}");  // If id is number this query sucks!!!
				}
				if (adName != null) {
					WHERE("AD.ad_name like ${adName}");
				}
				if (lastName != null) {
					WHERE("AD.ad_description like ${lastName}");
				}
				ORDER_BY("AD.ad_name");
			}
		}.toString();
	}

	public String updateAdSql() {
		return new SQL() {
			{ // New block!!!!
				UPDATE("ad_description");
				SET("ad_name = ${adName}");
				WHERE("id = ${id}");
			}
		}.toString();
	}
}
