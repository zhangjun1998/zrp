package com.zjcoding.zrpserver.web.service;

import com.zjcoding.zrpserver.web.dao.LogDao;
import com.zjcoding.zrpserver.web.entity.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/15
 * @Time 17:24
 */

@Service
public class LogService {

    @Autowired
    private LogDao logDao;

    /**
     * @Description 获取日志，按照授权码，端口，日期分类
     * @Date 17:26 2020/4/15
     * @Param []
     * @return java.util.List<com.zjcoding.zrpserver.web.entity.Log>
     **/
    public List<Log> getLogs(){
        return logDao.getLogs();
    }

    /**
     * @Description 插入日志
     * @Date 13:23 2020/4/16
     * @Param [clientKey, port, flow]
     * @return boolean
     **/
    public boolean addLog(String clientKey,int port,double flow){
        return logDao.addLog(clientKey,port,flow)>0;
    }

}
