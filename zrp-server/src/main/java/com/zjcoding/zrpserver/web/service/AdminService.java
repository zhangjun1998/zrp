package com.zjcoding.zrpserver.web.service;

import com.zjcoding.zrpserver.web.dao.AdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
        String md5Pass = DigestUtils.md5DigestAsHex(password.getBytes());
        return adminDao.login(username,md5Pass)!=null;
    }

    /**
     * @Description 修改密码
     * @Date 18:39 2020/4/15
     * @Param [username, oldPass, newPass]
     * @return boolean
     **/
    public boolean changePass(String username,String oldPass,String newPass){
        String md5OldPass = DigestUtils.md5DigestAsHex(oldPass.getBytes());
        String md5NewPass = DigestUtils.md5DigestAsHex(newPass.getBytes());
        return adminDao.changePass(username,md5OldPass,md5NewPass)>0;
    }

}
