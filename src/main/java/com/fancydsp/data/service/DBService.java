package com.fancydsp.data.service;


public interface DBService {
    Object loadMetaInfo(String database);


    Object queryBySql(String sql);
}
