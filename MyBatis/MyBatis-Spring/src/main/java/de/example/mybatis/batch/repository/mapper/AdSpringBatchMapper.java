package de.example.mybatis.batch.repository.mapper;

import de.example.mybatis.model.Ad;


public interface AdSpringBatchMapper /** extends MyBatisScanFilter NO SCAN BY MyBatis!! **/ {

	int insert(Ad record);
}
