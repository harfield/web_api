package com.fancydsp.data.service.impl;

import com.fancydsp.data.dao.job.JobMapper;
import com.fancydsp.data.service.DBService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@EnableAsync
@Service("jobService")
@MapperScan(value = "com.fancydsp.data.dao.job",sqlSessionFactoryRef = "jobSqlSessionFactory")
public class JobService {
    @Resource
    JobMapper jobMapper;

    @Resource
    DBService dbService;

    @Resource
    EmailService emailService;


    @Async
    public void downloadDdyReport(String sql,String emailAddr,String subject) {

        Object o = dbService.queryBySql(sql);
        //to csv

        emailService.sendSimpleMail(emailAddr,subject,o.toString());

    }

    public List<Map<String,Object>> queryBySql(String sql){
        return jobMapper.queryBySql(sql);

    }
}
