package com.fancydsp.data.web;

import com.fancydsp.data.service.impl.PrestoService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@EnableAsync
@RestController
@RequestMapping("/presto")
@ResponseBody
public class PrestoController {

    @Resource
    private PrestoService service;

    @GetMapping("/job/{id}")
    Object asyncJob(@PathVariable int id){
        Object res  = null;
        try {
           res =  service.query("show tables");
        }catch (Exception e){
            return e.getMessage();
        }
       return res;

    }
}
