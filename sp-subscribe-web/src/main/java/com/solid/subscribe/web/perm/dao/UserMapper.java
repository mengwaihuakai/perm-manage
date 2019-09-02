package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * Created by ZOOMY on 2018/8/8.
 */

@Mapper
public interface UserMapper {
    /*根据id查询用户*/
    User getUserById(Integer id);
    /*根据account查询用户*/
    User getUserByAccount(String account);
    /*查询用户*/
    List<User> getUsers(User userParam);
    /*更新user*/
    Integer updateUser(User user);
    /*创建用户*/
    Integer saveUser(User user);
    /*根据用户ID删除*/
    Integer deleteById(Integer id);

}
