package com.zjcoding.zrpserver.web.service;

import com.zjcoding.zrpserver.web.dao.AdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 13:43
 */

@Service
public class AdminService {

    @Autowired
    private AdminDao adminDao;

    /**
     * @Description 管理员登录
     * @Date 16:35 2020/4/13
     * @Param [admin]
     * @return boolean
     **/
    public boolean login(String username,String password){
        return adminDao.login(username,password)!=null;
    }

    /**
     * @Description 修改密码
     * @Date 18:39 2020/4/15
     * @Param [username, oldPass, newPass]
     * @return boolean
     **/
    public boolean changePass(String username,String oldPass,String newPass){
        return adminDao.changePass(username,oldPass,newPass)>0;
    }

}
