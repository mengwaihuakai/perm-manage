package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Mapper
public interface RoleMapper {
    Set<Role> getRolesByUserId(Integer userId);
}
