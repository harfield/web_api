package com.fancydsp.data.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CommonMapper {

    @Select("${sql}")
    List<Map<String,Object>> queryBySql(@Param("sql") String sql);
}