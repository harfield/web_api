package com.fancydsp.data.web;


import com.fancydsp.data.domain.User;
import com.fancydsp.data.service.DBService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/user")
public class UserController  {
    @Resource
    DBService dbService;
    @GetMapping
    User home() {
//        return dbService.getUserInfo(1) ;
        return null;
    }

}
