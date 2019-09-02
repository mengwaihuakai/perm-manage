package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserRoleMapper {
    Integer saveUserRoleList(List<UserRole> userRoleList);
    List<UserRole> getUserRoles();
    List<Integer> getRoleIdByUserId(Integer userId);
    List<Integer> getUserIdByRoleId(Integer roleId);
    Integer deleteByUserId(Integer userId);
    Integer deleteByRoleId(Integer roleId);
    Integer deleteByRoleIdList(Map<String,Object> paramMap);
}
