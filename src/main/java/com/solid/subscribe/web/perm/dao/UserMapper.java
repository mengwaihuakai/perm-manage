package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;


/**
 * Created by ZOOMY on 2018/8/8.
 */

@Mapper
public interface UserMapper {
    /*根据id查询用户*/
    User getUserById(Integer id);
    /*根据account查询用户*/
    User getUserByAccount(String account);
    /*查询所有用户*/
    List<User> getUsers();
    /*更新user*/
    Integer updateUser(User user);
    /*创建用户*/
    Integer saveUser(User user);
    /*根据用户ID删除*/
    Integer deleteById(Integer id);
    /*根据用户ID查询权限代码*/
    Set<String> getPermCodesByUserId(Integer id);

}
