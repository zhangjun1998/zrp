package com.zjcoding.zrpserver.common.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Description 配置解析器
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 15:13
 */

public class ConfigParser {

    private static Map<String,Object> config = null;
    private static ArrayList<Map<String,Object>> portArr = null;

    public ConfigParser() {
        try {
            InputStream in;

            //在IDE启动获取配置文件
            //File file = ResourceUtils.getFile("classpath:config/zrp-server.yaml");
            //in = new FileInputStream(file);

            // 获取外部配置文件
            String dir = System.getProperty("user.dir");
            File file = new File(dir+File.separator+"zrp-server.yaml");
            in = new FileInputStream(file);

            Yaml yaml = new Yaml();
            config = (Map<String,Object>) yaml.load(in);
            portArr = (ArrayList<Map<String, Object>>) get("config");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Object get(String key){
        if (config==null){
            new ConfigParser();
        }
        return config.get(key);
    }

    public static ArrayList<Map<String,Object>> getPortArray(){
        if (portArr==null){
            new ConfigParser();
        }
        return portArr;
    }

}
