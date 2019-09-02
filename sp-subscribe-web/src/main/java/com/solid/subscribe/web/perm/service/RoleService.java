package com.solid.subscribe.web.perm.service;

import com.solid.subscribe.web.perm.dao.PermissionMapper;
import com.solid.subscribe.web.perm.dao.RoleMapper;
import com.solid.subscribe.web.perm.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Service
public class RoleService {
    @Autowired(required = false)
    RoleMapper roleMapper;
    @Autowired(required = false)
    PermissionMapper permissionMapper;
    @Autowired
    MoniLogService moniLogService;


    public Set<Role> getRolesByUserId(Integer userId) throws Exception {
        return roleMapper.getRolesByUserId(userId);
    }

}
