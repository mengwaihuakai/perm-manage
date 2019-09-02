package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RolePermissionMapper {
    Integer saveRolePermList(List<RolePermission> rolePermissionList);
    Integer deleteByRoleId(Integer roleId);
    List<RolePermission> getRolePerms();
    List<Integer> getPermIdByRoleId(Integer roleId);
    Integer deleteByPermIdList(Map<String,Object> paramMap);
}
