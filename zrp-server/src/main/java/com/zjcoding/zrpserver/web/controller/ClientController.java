package com.zjcoding.zrpserver.web.controller;

import com.zjcoding.zrpserver.web.entity.Client;
import com.zjcoding.zrpserver.web.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 13:59
 */

@RestController
@RequestMapping("/api/client")
@CrossOrigin
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * @Description 添加用户
     * @Date 16:47 2020/4/13
     * @Param [client]
     * @return boolean
     **/
    @PostMapping("/add")
    public boolean addClient(@RequestBody Client client){
        return clientService.addClient(client);
    }

    /**
     * @Description 删除用户
     * @Date 16:48 2020/4/13
     * @Param [id]
     * @return boolean
     **/
    @RequestMapping("/delete")
    public boolean delete(@RequestParam("clientId") int id){
        return clientService.deleteClient(id);
    }

    /**
     * @Description 查询所有用户
     * @Date 16:49 2020/4/13
     * @Param []
     * @return java.util.List<com.zjcoding.zrp.web.entity.Client>
     **/
    @RequestMapping("/get")
    public List<Client> getClients(){
        return clientService.getClients();
    }

}
