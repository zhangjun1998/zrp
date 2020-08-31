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
            //IDEA中运行
            File file = getProjectConfigFile();
            //打包到服务器
            //File file = getServerConfigFile();
            InputStream in = new FileInputStream(file);

            Yaml yaml = new Yaml();
            config = (Map<String,Object>) yaml.load(in);
            portArr = (ArrayList<Map<String, Object>>) get("config");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 在IDE启动获取配置文件
     * @Description
     * @Author ZhangJun
     * @Date 13:28 2020/8/29
     * @Param
     * @return
     */
    public File getProjectConfigFile() throws Exception{
        return ResourceUtils.getFile("classpath:config/zrp-server.yaml");
    }


    /**
     * 服务器中启动获取配置文件
     * @Description
     * @Author ZhangJun
     * @Date 13:31 2020/8/29
     * @Param
     * @return
     */
    public File getServerConfigFile()
    {
        String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        classPath = classPath.substring(5,classPath.indexOf("zrp-server.jar"))+"zrp-server.yaml";
        return new File(classPath);
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
