package de.example.mybatis.repository.mapper;

import java.util.List;

import de.example.mybatis.mapper.filter.MyBatisScanFilter;
import de.example.mybatis.model.Ad;


public interface ParentMapper extends MyBatisScanFilter {

	List<Ad> selectAdsParent();
}
