package com.zjcoding.zrpserver.web.service;

import com.zjcoding.zrpserver.web.dao.ClientDao;
import com.zjcoding.zrpserver.web.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 13:59
 */

@Service
public class ClientService {

    @Autowired
    private ClientDao clientDao;

    /**
     * @Description 添加用户
     * @Date 16:37 2020/4/13
     * @Param [client]
     * @return boolean
     **/
    public boolean addClient(Client client){
        return clientDao.addClient(client)>0;
    }

    /**
     * @Description 删除用户
     * @Date 16:39 2020/4/13
     * @Param [id]
     * @return boolean
     **/
    public boolean deleteClient(int id){
        return clientDao.deleteClient(id)>0;
    }

    /**
     * @Description 查询所有用户
     * @Date 16:39 2020/4/13
     * @Param []
     * @return java.util.List<com.zjcoding.zrp.web.entity.Client>
     **/
    public List<Client> getClients(){
        return clientDao.getClients();
    }

    /**
     * @Description 核查clientKey
     * @Date 13:00 2020/4/14
     * @Param [clientKey]
     * @return boolean
     **/
    public boolean checkClientKey(String clientKey){
        return clientDao.checkClientKey(clientKey)!=null;
    }

}
