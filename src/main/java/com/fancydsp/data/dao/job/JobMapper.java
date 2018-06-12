package com.fancydsp.data.dao.job;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface JobMapper {

    @Select("${sql}")
    List<Map<String,Object>> queryBySql(@Param("sql") String sql);
}
