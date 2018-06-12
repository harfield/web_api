package com.fancydsp.data.service.impl;


import com.fancydsp.data.dao.report.CommonMapper;
import com.fancydsp.data.service.DBService;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service(value="dbService")
@MapperScan(value = "com.fancydsp.data.dao.report")
public class DBServiceImpl implements DBService{
    private static Logger log = LoggerFactory.getLogger(DBServiceImpl.class);
    @Resource
    CommonMapper commonDao;
    @Resource(name = "primaryDataSource")
    private DataSource dataSource;

    @Override
    public Object loadMetaInfo(String database) {
        Map<String,Map<String,Object>> dbInfo = new TreeMap<String,Map<String,Object>>();
        Connection connection = null;
        ResultSet columns = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            columns = metaData.getColumns(database,"","","");

            while (columns.next()){
                String tableName = columns.getString(3);
                Map<String,Object> column = null;
                if(!dbInfo.containsKey(tableName)){
                    dbInfo.put(tableName,new HashMap<String,Object>());
                }
                column = dbInfo.get(tableName);
                column.put(columns.getString(4),columns.getString(12));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(),e);
        }finally {
            if(columns != null){
                try {
                    columns.close();
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }

            }
            if(connection != null){
                try {
                    connection.close();
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }

            }
        }
        return dbInfo;
    }



    @Override
    public Object queryBySql(String sql) {
        return commonDao.queryBySql(sql);
    }
}
