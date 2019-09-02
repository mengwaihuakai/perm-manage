package com.solid.subscribe.web.perm.dao;


import com.solid.subscribe.web.perm.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Mapper
public interface PermissionMapper {
    Set<Permission> getPermissionsByRoleId(Integer roleId);
}
