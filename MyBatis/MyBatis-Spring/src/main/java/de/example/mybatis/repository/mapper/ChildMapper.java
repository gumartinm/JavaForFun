package de.example.mybatis.repository.mapper;

import java.util.List;

import de.example.mybatis.model.Ad;


public interface ChildMapper extends ParentMapper {

	List<Ad> selectAdsChild();
}
