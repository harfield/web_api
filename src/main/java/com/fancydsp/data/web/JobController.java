package com.fancydsp.data.web;

import com.fancydsp.data.domain.ResponseMessage;
import com.fancydsp.data.service.impl.JobService;
import com.fancydsp.data.utils.MysqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/jobs/")
@ResponseBody
public class JobController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private JobService service;


    @RequestMapping("/{id}")
    Object asyncJob(@PathVariable int id
                    ,@RequestParam("begin_date") String beginDate
                    ,@RequestParam("end_date") String endDate
                    ,@RequestParam("email") String email
                    ,@RequestParam("username") String username
    ){
        logger.info("request params are : reportId {},beginDate {},endDate {}, email {}, username {}",id,beginDate,endDate,email,username);

        String sql = MysqlBuilder.build()
                .SELECT("script,name,fields")
                .FROM("fancy_report_task.report_sql")
                .WHERE("id="+id).toString();
        List<Map<String, Object>> rows = service.queryBySql(sql);
        if(rows.size()==0){
            throw new RuntimeException("no sql found ");
        }else if(rows.size() > 1){
            throw new RuntimeException( rows.size() + "  scripts found,this should not happen ");
        }

        String script = rows.get(0).get("script").toString().replaceAll("\\$\\{begin_date}",beginDate).replaceAll("\\$\\{end_date}",endDate);
        String[] fields = rows.get(0).get("fields").toString().split("\\n");
        Map<String,String> tMap=new LinkedHashMap<String,String>();
        for(String w :fields){
            String[] split = w.split("=");
            tMap.put(split[0],split[1]);
        }
        String subject = rows.get(0).get("name")+"_"+beginDate+"_"+endDate;
        try{
            service.downloadDdyReport(script,email,subject.replace("-",""),tMap);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        return new ResponseMessage("submit");
    }
}
