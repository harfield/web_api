package com.fancydsp.data.web;


import com.fancydsp.data.utils.MysqlBuilder;
import com.fancydsp.data.service.DBService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/data")
@ResponseBody
public class DataController {
    @Resource
    DBService dbService;


    @GetMapping("/meta")
    Object loadTableInfo(){
        return  dbService.loadMetaInfo("dowsing_data");
    }

    @RequestMapping("/{name}")
    Object queryByTableName(@PathVariable String name
            ,@RequestParam(defaultValue = "1") int page
            ,@RequestParam(defaultValue = "10") int size
            ,@RequestParam(defaultValue = "*") String[] fields
    ){
        String sql = MysqlBuilder.build()
                .SELECT(fields)
                .FROM(name)
                .toString();
        Object o = dbService.queryBySql(sql);
        return o;
    }

}