package com.zjcoding.zrp.common.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Description 配置解析器
 * @Author zhangjun
 * @Data 2020/2/22
 * @Time 16:51
 */

public class ConfigParser {

    private static Map<String,Object> config = null;
    private static ArrayList<Map<String,Object>> portArr = null;

    public ConfigParser() {
        try {
            //定位当前文件夹路径(zrp)
            String dir = System.getProperty("user.dir");

            File file = new File(dir+File.separator+"proxy-config.yaml");
            InputStream in = new FileInputStream(file);
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
