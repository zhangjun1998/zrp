/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/15
 * @Time 16:01
 */

package com.zjcoding.zrpserver.web.dao;

import com.zjcoding.zrpserver.web.entity.Log;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogDao {

    /**
     * @Description 获取所有日志，按照授权码，端口，日期分类
     * @Date 17:25 2020/4/15
     * @Param []
     * @return java.util.List<com.zjcoding.zrpserver.web.entity.Log>
     **/
    List<Log> getLogs();

    int addLog(@Param("clientKey") String clientKey,@Param("port") int port,@Param("flow") double flow);

}
