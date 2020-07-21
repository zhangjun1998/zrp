/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 13:36
 */

package com.zjcoding.zrpserver.web.dao;

import com.zjcoding.zrpserver.web.entity.Client;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientDao {

    /**
     * @Description 添加用户
     * @Date 16:23 2020/4/13
     * @Param [client]
     * @return int
     **/
    int addClient(@Param("client") Client client);

    /**
     * @Description 删除客户
     * @Date 16:23 2020/4/13
     * @Param [client]
     * @return int
     **/
    int deleteClient(@Param("id") int id);

    /**
     * @Description 查询所有客户
     * @Date 16:23 2020/4/13
     * @Param []
     * @return java.util.List<com.zjcoding.zrp.web.entity.Client>
     **/
    List<Client> getClients();

    Client checkClientKey(@Param("clientKey") String clientKey);

}
