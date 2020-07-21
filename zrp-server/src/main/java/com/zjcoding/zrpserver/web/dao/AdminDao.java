/**
 * @Description
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 13:36
 */

package com.zjcoding.zrpserver.web.dao;

import com.zjcoding.zrpserver.web.entity.Admin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDao {

    /**
     * @Description 登录
     * @Date 16:22 2020/4/13
     * @Param [username, password]
     * @return com.zjcoding.zrp.web.entity.Admin
     **/
    Admin login(@Param("username") String username, @Param("password") String password);

    /**
     * @Description 修改密码
     * @Date 18:35 2020/4/15
     * @Param []
     * @return int
     **/
    public int changePass(@Param("username") String username,@Param("oldPass") String oldPass,@Param("newPass") String newPass);

}
