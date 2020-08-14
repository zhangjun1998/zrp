package com.zjcoding.zrpserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@MapperScan("com.zjcoding.zrpserver.web.dao")
@SpringBootApplication
public class ZrpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZrpServerApplication.class, args);
    }
}
