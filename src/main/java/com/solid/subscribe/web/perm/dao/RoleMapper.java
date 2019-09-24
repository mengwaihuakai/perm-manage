package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Mapper
public interface RoleMapper {
    Set<String> getRoleCodesByUserId(Integer userId);
    /*查询所有有效角色*/
    List<Role> getValidRoles();
    /*条件查询所有角色*/
    List<Role> getRoles(Role roleParam);
    /*根据name查询角色*/
    Role getRoleByName(String name);
    /*根据id查询*/
    Role getRoleById(Integer id);
    /*更新角色*/
    Integer updateRole(Role user);
    /*创建角色*/
    Integer saveRole(Role user);
    /*根据角色ID删除*/
    Integer deleteById(Integer id);
}
