package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * Created by ZOOMY on 2018/8/8.
 */

@Mapper
public interface UserMapper {
    /*根据id查询用户*/
    User getUserById(Integer id);
    /*根据account查询用户*/
    User getUserByAccount(String account);
    /*更新user*/
    Integer updateUser(User user);

}
