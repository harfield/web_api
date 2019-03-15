package com.fancydsp.data.web;

import com.alibaba.fastjson.JSONObject;
import com.fancydsp.data.domain.Job.OfflineSqlTask;
import com.fancydsp.data.domain.ResponseMessage;
import com.fancydsp.data.service.impl.JobService;
import com.fancydsp.data.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@RestController
@RequestMapping("/jobs/")
@ResponseBody
public class JobController {
    Logger logger = LoggerFactory.getLogger(getClass());


    @Resource
    private JobService service;


    @PostMapping("/ddy")
    Object sendOfflineMail(@RequestParam("sql") String sql
            , @RequestParam("dsn") String dsn
            , @RequestParam("receiver") String receiver
            , @RequestParam("subject") String subject
            , @RequestParam Map<String, String> params
    ) {
        logger.info("sql : {} ", sql);
        logger.info("request params are : subject {}, email {} ,user {}"
                , subject, receiver, params.get("user_info")
        );
        JSONObject dbInfo = JSONObject.parseObject(dsn);
        service.sendDdyReportByMail(sql, dbInfo.getString("dsn"), receiver, subject);
        return new ResponseMessage("submit");
    }

    @RequestMapping("/script/{id}")
    Object startScript(@PathVariable("id") int id
            , @RequestParam Map<String, String> params
    ) throws ExecutionException, InterruptedException {
        logger.info("script {} ,params :{}", id, params.toString());
        OfflineSqlTask offlineSqlTask = service.fetchJobInfo(id);

        String identifier = Utils.MD5(offlineSqlTask.getScript() + JSONObject.toJSONString(params));
        Map<String, Object> jobData = (Map<String, Object>) service.getJobData(identifier);

        if (jobData == null) {
            startJob(params, offlineSqlTask, identifier);
            return new ResponseMessage("submit");
        } else {
            Future future = (Future) jobData.get("future");
            if (future.isDone()) {
                jobData.put("last_status", future.get());
                if (System.currentTimeMillis() - (long) jobData.get("start") > 15 * 60 * 1000L) {
                    startJob(params, offlineSqlTask, identifier);
                    return new ResponseMessage("submit");
                } else {
                    return new ResponseMessage(
                            String.format("请稍后重试,上一次执行时间 ： %s , status :%s"
                                    , new Date((long) jobData.get("start"))
                                    ,jobData.get("last_status").toString())
                            , 100
                    );
                }
            } else {
                jobData.put("msg", "running");
                jobData.put("code",110);
            }
            return jobData;
        }


    }

    private void startJob(Map<String, String> params, OfflineSqlTask offlineSqlTask, String identifier) {
        Map<String, Object> jobData;
        jobData = new HashMap<String, Object>();
        Future<String> future = service.runScript(offlineSqlTask.getScript(), params);
        jobData.put("start", System.currentTimeMillis());
        jobData.put("future", future);
        service.setJobData(identifier, jobData);
    }


    @RequestMapping("/sql/{id}")
    Object sendMailBySql(@PathVariable int id
            , @RequestParam("email") String email
            , @RequestParam("username") String username
            , @RequestParam Map<String, String> params
    ){
        logger.info("request params are : reportId {}, email {}, username {}, params {}  "
                ,id,email,username,params.toString()
        );

        OfflineSqlTask taskInfo = service.fetchJobInfo(id);
        if(null == taskInfo) {
            throw new RuntimeException(" no job info found :  "  + id);
        }

        List<OfflineSqlTask.Rule> rules = taskInfo.getRules();
        Map<String,OfflineSqlTask.Rule> ruleName = new HashMap<String,OfflineSqlTask.Rule>();
        Map<String,Object> sqlParams = new HashMap<String,Object>();
        for(OfflineSqlTask.Rule rule : rules){
            ruleName.put(rule.getSqlPlaceholder(),rule);
            setParamWithType(sqlParams,params,rule);
        }

        String rawSql = taskInfo.getScript();
        for(String key : sqlParams.keySet()){
            rawSql = rawSql.replaceAll("\\$\\{"+key+"}",ruleName.get(key).getReplaceValue());
        }

        rawSql = rawSql.replaceAll("\\$\\{.*?}","");
        String fields = taskInfo.getFields();
        Map<String,String> fieldMap = new HashMap<String,String>();
        for(String kf : fields.split("\n",-1)){
            String[] split = kf.split("=", -1);
            fieldMap.put(split[0],split[1]);
        }
        try {
            service.downloadDdyReport(rawSql, email, taskInfo.getName(), fieldMap, sqlParams);
        }catch (Exception e){
            //no use
        }

        return new ResponseMessage("submit");
    }
    private void setParamWithType(Map<String, Object> sqlParams, Map<String, String> inputParams, OfflineSqlTask.Rule rule) {
        String rawParamValue = inputParams.get(rule.getSqlPlaceholder());
        Object paramValue = null;

        if(null != rawParamValue) {
            switch (rule.getParamType()) {
                case 1: //string
                    paramValue = rawParamValue;
                    break;
                case 2: //int
                    paramValue = Integer.parseInt(rawParamValue);
                    break;
                case 3: //bool
                    paramValue = rule.getReplaceValue();
                    break;
                default:
                    throw new RuntimeException(" param type " + rule.getParamType() + "not support yet" );

            }
        }

        if(rule.getIsOptional() ){
            if(paramValue != null){
                sqlParams.put(rule.getSqlPlaceholder(),paramValue);
            }
        }else{
            if(paramValue == null){
                throw new RuntimeException(" param " + rule.getSqlPlaceholder() + " not found which is needed ");
            }
            sqlParams.put(rule.getSqlPlaceholder(),paramValue);
        }

    }


}

