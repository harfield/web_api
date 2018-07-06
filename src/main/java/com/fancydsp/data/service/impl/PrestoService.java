package com.fancydsp.data.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "prestoService")
public class PrestoService {

    Connection psConn ;
    @Bean(name="prestoConnection")
    Connection getPrestoConnection(@Value("${presto.connection.driver}") String driver
                                  ,@Value("${presto.connection.url}") String url
                                  ,@Value("${presto.connection.user}") String user
                                  ,@Value("${presto.connection.password}") String password
    ) throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        psConn = DriverManager.getConnection(url,user,password);
        return psConn;
    }
    @PreDestroy
    public void destroy() throws SQLException {
        psConn.close();
    }

    @Async
    public AsyncResult<List<Map<String,Object>>> query(String sql) throws SQLException {
        Statement statement = psConn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
        while (resultSet.next()){
            Map<String,Object> line = new HashMap<String,Object>();
            for(int i = 1;i<=count;i++){
               line.put(metaData.getColumnLabel(i),resultSet.getObject(i));
            }
            res.add(line);
        }
        resultSet.close();
        statement.close();
        return  new AsyncResult<List<Map<String,Object>>>(res);
    }
    @Async
    public boolean execute(String sql){
        return true;
    }



}
