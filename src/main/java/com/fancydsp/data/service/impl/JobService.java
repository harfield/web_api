package com.fancydsp.data.service.impl;

import com.fancydsp.data.dao.job.JobMapper;
import com.fancydsp.data.domain.Pair;
import com.fancydsp.data.service.DBService;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@EnableAsync
@Service("jobService")
@MapperScan(value = "com.fancydsp.data.dao.job",sqlSessionFactoryRef = "jobSqlSessionFactory")
public class JobService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    JobMapper jobMapper;

    @Resource
    DBService dbService;

    @Resource
    EmailService emailService;


    @Async
    public void downloadDdyReport(String sql,String emailAddr,String subject,Map<String,String> heads) throws IOException {
        Object o = dbService.queryBySql(sql);
        StringBuilder builder = new StringBuilder();
        String fileName = subject;
        File tmpFile = File.createTempFile( subject,".csv.zip");
        FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
        CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
        ZipOutputStream out = new ZipOutputStream(cos);
        out.putNextEntry(new ZipEntry(fileName + ".csv"));
        int size = heads.size();
        int i=0;

        for (String v : heads.values()){
            builder.append('"').append(v).append('"');
            i++;
            if(i==size){
                builder.append("\n");
            }else {
                builder.append(",");
            }
        }
        for(Object line : (List) o){
            Map<String, Object> row = (Map<String, Object>) line;
            i=0;
            for (String key : heads.keySet()){
                Object v = row.get(key);
                if(v == null){
                    v = "";
                }else{
                    v = v.toString().replace("\""," ");
                }
                builder.append('"').append(v).append('"');
                i++;
                if(i==size){
                    builder.append("\n");
                }else {
                    builder.append(",");
                }

            }
            if(builder.length() > 1024*1024){
               out.write( builder.toString().getBytes("GBK"));
               builder.setLength(0);
            }

        }
        if(builder.length() > 0){
            out.write( builder.toString().getBytes("GBK"));
            builder.setLength(0);
        }
        out.close();
       // builder.close();
        List<Pair<String,File>> attachments = new ArrayList<Pair<String,File>>();
        attachments.add(new Pair<String,File>(subject.substring(0,4)+".csv.zip",tmpFile));
        emailService.sendAttachmentsMail(emailAddr,subject,subject,attachments);
        logger.info("file size : {}",tmpFile.length());
        tmpFile.delete();

    }

    public List<Map<String,Object>> queryBySql(String sql){
        return jobMapper.queryBySql(sql);

    }
}
