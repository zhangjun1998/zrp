package com.zjcoding.zrpserver.web.controller;

import com.zjcoding.zrpserver.web.entity.Log;
import com.zjcoding.zrpserver.web.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/15
 * @Time 17:27
 */

@RestController
@RequestMapping("/api/log")
@CrossOrigin
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * @Description 获取日志，按照授权码，端口，日期分类
     * @Date 17:28 2020/4/15
     * @Param []
     * @return java.util.List<com.zjcoding.zrpserver.web.entity.Log>
     **/
    @RequestMapping("/get")
    public List<Log> getLogs(){
        return logService.getLogs();
    }

}
