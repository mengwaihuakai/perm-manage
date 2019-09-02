package com.solid.subscribe.web.perm.service;

import com.solid.subscribe.web.perm.dao.PermissionMapper;
import com.solid.subscribe.web.perm.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class PermissionService {

    @Autowired
    PermissionMapper permissionMapper;

    public Set<Permission> getPermissionsByRoleId(Integer roleId) {
        return permissionMapper.getPermissionsByRoleId(roleId);
    }

    /**
     * 根据权限所属菜单类别进行分类
     */

    public List<Permission> getValidPermissions() {
        //所有权限
        return permissionMapper.getValidPermissions();
    }
}
