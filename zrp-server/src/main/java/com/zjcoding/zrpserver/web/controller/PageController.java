package com.zjcoding.zrpserver.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author ZhangJun
 * @Data 2020/7/24 16:43
 */

@Controller
@RequestMapping("/zrp")
public class PageController {

    @RequestMapping("/index")
    public String goIndex(){
        return "index";
    }

    @RequestMapping("/admin")
    public String goAdmin(){
        return "admin";
    }

    @RequestMapping("/client")
    public String goClient(){
        return "client";
    }

    @RequestMapping("/log")
    public String goLog(){
        return "log";
    }

    @RequestMapping("/login")
    public String goLogin(){
        return "login";
    }

}
