package com.fancydsp.data.service.impl;

import com.fancydsp.data.dao.job.JobMapper;
import com.fancydsp.data.domain.Pair;
import com.fancydsp.data.service.DBService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    public void downloadDdyReport(String sql,String emailAddr,String subject,Map<String,String> heads) throws IOException {
        long id = Thread.currentThread().getId();
        Object o = dbService.queryBySql(sql);
        File tmpFile = File.createTempFile( subject+id,".csv");
        BufferedWriter bfw = new BufferedWriter(new FileWriter(tmpFile));
        int size = heads.size();
        int i=0;

        for (String v : heads.values()){
            bfw.append('"').append(v).append('"');
            i++;
            if(i==size){
                bfw.append("\n");
            }else {
                bfw.append(",");
            }
        }
        for(Object line : (List) o){
            Map<String, Object> row = (Map<String, Object>) line;
            i=0;
            for (String key : heads.keySet()){
                bfw.append('"').append(row.get(key)+"").append('"');
                i++;
                if(i==size){
                    bfw.append("\n");
                }else {
                    bfw.append(",");
                }

            }
        }
        bfw.close();
        List<Pair<String,File>> attachments = new ArrayList<Pair<String,File>>();
        attachments.add(new Pair<String,File>(subject.substring(0,4)+".csv",tmpFile));
        emailService.sendAttachmentsMail(emailAddr,subject,subject,attachments);
        tmpFile.delete();

    }

    public List<Map<String,Object>> queryBySql(String sql){
        return jobMapper.queryBySql(sql);

    }
}
