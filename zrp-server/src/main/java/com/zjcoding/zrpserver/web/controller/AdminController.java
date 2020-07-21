package com.zjcoding.zrpserver.web.controller;

import com.zjcoding.zrpserver.web.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 13:39
 */

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * @Description 管理员登录
     * @Date 16:45 2020/4/13
     * @Param [username, password]
     * @return boolean
     **/
    @RequestMapping("/login")
    public boolean login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request){
        boolean flag = adminService.login(username,password);
        if (flag){
            request.getSession().setAttribute("user",username);
        }
        return flag;
    }

    /**
     * @Description 修改密码
     * @Date 18:34 2020/4/15
     * @Param [username, oldPass, newPass]
     * @return boolean
     **/
    @RequestMapping("/changePass")
    public boolean changePass(@RequestParam("username") String username,
                              @RequestParam("oldPass") String oldPass,
                              @RequestParam("newPass") String newPass){
        return adminService.changePass(username,oldPass,newPass);
    }

    /**
     * @Description 检查登录状态
     * @Date 17:18 2020/4/13
     * @Param [request]
     * @return java.lang.String
     **/
    @RequestMapping("/isLogin")
    public String isLogin(HttpServletRequest request){
        Object user = request.getSession().getAttribute("user");
        if (user!=null){
            return (String) user;
        }
        return "null";
    }

}
