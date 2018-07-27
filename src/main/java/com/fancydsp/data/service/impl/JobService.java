package com.fancydsp.data.service.impl;

import com.fancydsp.data.dao.job.JobMapper;
import com.fancydsp.data.domain.Job.OfflineSqlTask;
import com.fancydsp.data.domain.Pair;
import com.fancydsp.data.service.DBService;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

    Map<String, Object> jobData = new ConcurrentHashMap<String, Object>();

    public OfflineSqlTask fetchJobInfo (long id){
        return jobMapper.fetchSqlJobTask(id);
    }


    @Async
    public void downloadDdyReport(String sql,String emailAddr,String subject,Map<String,String> heads) throws IOException {
        Object o = dbService.queryBySql(sql);
        sendMail(emailAddr, subject, heads, (List) o);

    }


    @Async
    public void downloadDdyReport(String sql,String emailAddr,String subject,Map<String,String> heads,Map<String,Object> sqlParams) throws IOException {
        Object o = dbService.queryBySql(sql,sqlParams);
        sendMail(emailAddr, subject, heads, (List) o);

    }

    @Async
    public void sendDdyReportByMail(String sql, String dsn, String email, String subject) {

        String url = "jdbc:mysql://" + dsn.substring(dsn.indexOf("(") + 1).replace(")", "") + "?useUnicode=true&characterEncoding=UTF-8";
        String[] token = dsn.substring(0, dsn.indexOf("@")).split(":", -1);
        try {
            Connection connection = DriverManager.getConnection(url, token[0], token[1]);
            PreparedStatement psm = connection.prepareStatement(sql);
            ResultSet resultSet = psm.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            Map<String, String> heads = new LinkedHashMap<String, String>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                heads.put(metaData.getColumnLabel(i), metaData.getColumnLabel(i));
            }
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            while (resultSet.next()) {
                Map<String, Object> item = new HashMap<String, Object>();
                for (String columnName : heads.values()) {
                    item.put(columnName, resultSet.getObject(columnName));
                }
                records.add(item);
            }
            resultSet.close();
            psm.close();
            connection.close();
            sendMail(email, subject, heads, records);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Async
    public Future<String> runScript(String command, Map<String, String> params) {
        Long timeout = 30 * 60 * 1000L;
        String param = "";
        for (String key : params.keySet()) {
            param += key.trim() + "=" + params.get(key) + ";";
        }
        command = " eval " + param + " " + command;

        try {
            InputStream errInputStream;

            String[] cmd;
            String osName = System.getProperty("os.name");
            if (osName.startsWith("Windows")) {
                cmd = new String[3];
                if (osName.equals("Windows 95")) { // windows 95 only
                    cmd[0] = "command.com";
                } else {
                    cmd[0] = "cmd.exe";
                }
                cmd[1] = "/C";
                cmd[2] = command;
            } else if (osName.equals("Linux") || osName.contains("Mac")) {
                cmd = new String[3];
                cmd[0] = "/bin/sh";
                cmd[1] = "-c";
                cmd[2] = command;
            } else {
                cmd = new String[1];
                cmd[0] = command;
            }
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            errInputStream = proc.getErrorStream();
            boolean exitStatus = waitFor(proc, timeout, TimeUnit.MILLISECONDS);
            if (!exitStatus) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                if (errInputStream != null) {
                    try {
                        while (errInputStream.available() > 0) {
                            int i = errInputStream.read(bytes, 0, bytes.length);
                            if (i < 0) {
                                break;
                            }
                            outputStream.write(bytes, 0, i);
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.error(new String(outputStream.toByteArray(), "utf8"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            emailService.sendSimpleMail(null, "web-api running script error ", "execute :" + command + "failed");
            return new AsyncResult<String>(e.getMessage());
        }
        return new AsyncResult<String>("OK");
    }

    private boolean waitFor(Process proc, long timeout, TimeUnit unit) throws InterruptedException {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                return proc.exitValue() == 0;
            } catch (IllegalThreadStateException ex) {
                if (rem > 0) {
                    Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
                }
            }
            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        try {
            return proc.exitValue() == 0;
        } catch (IllegalThreadStateException ex) {
            proc.destroy();
            return false;
        }
    }


    private void sendMail(String emailAddr, String subject, Map<String, String> heads, List o) throws IOException {
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
        for(Object line : o){
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
        List<Pair<String,File>> attachments = new ArrayList<Pair<String,File>>();
        attachments.add(new Pair<String,File>(subject.substring(0,4)+".csv.zip",tmpFile));
        emailService.sendAttachmentsMail(emailAddr,subject,subject,attachments);
        logger.info("file size : {}",tmpFile.length());
        tmpFile.delete();
    }

    public synchronized Object getJobData(String id) {
        return jobData.get(id);
    }

    public synchronized void setJobData(String id, Object data) {
        jobData.put(id, data);
    }




}
