package de.example.mybatis.repository.mapper;

import java.util.List;
import java.util.Set;

import de.example.mybatis.mapper.filter.MyBatisScanFilter;
import de.example.mybatis.model.Ad;

public interface AdMapper extends MyBatisScanFilter {

	Set<Ad> selectAsSet();
	
	List<Ad> selectAsList();
	
	long insert(Ad record);
	
	long updateByPrimaryKey(Ad record);
}
