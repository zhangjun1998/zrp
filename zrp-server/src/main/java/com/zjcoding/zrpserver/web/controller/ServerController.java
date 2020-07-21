package com.zjcoding.zrpserver.web.controller;

import com.zjcoding.zrpserver.server.ServerRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;

/**
 * @Description zrp-server启动入口
 * @Author zhangjun
 * @Data 2020/4/15
 * @Time 14:13
 */

@Controller
public class ServerController {

    @Autowired
    private ServerRun serverRun;

    // 这里有问题要注意，用静态变量加上初始化方法的形式来调整代码执行顺序，
    // 使得serverRun能够成功注入对象，否则会导致注入失败，空指针异常
    private static ServerController serverController;

    @PostConstruct
    public void init(){
        serverController = this;
        serverController.serverRun = this.serverRun;
        System.out.println("初始化完成");
        try {
            System.out.println("zrp启动");
            serverController.serverRun.start();
        }catch (Exception e){
            System.out.println("zrp-server启动失败");
            e.printStackTrace();
        }
    }

}
