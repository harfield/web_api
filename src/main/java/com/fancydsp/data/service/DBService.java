package com.fancydsp.data.service;


import java.util.Map;

public interface DBService {
    Object loadMetaInfo(String database);


    Object queryBySql(String sql);


    Object queryBySql(String sql , Map<String,Object> params);
}
