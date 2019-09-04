package com.solid.subscribe.web.perm.vo;

import com.solid.subscribe.web.perm.entity.Permission;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class RolePageInfo {
    /*角色ID*/
    private int id;
    /*角色名称*/
    private String name;
    /*角色代码*/
    private String code;
    /*角色状态*/
    private int status;
    /*创建时间*/
    private Timestamp createTime;
    /*更新时间*/
    private Timestamp updateTime;
    /*权限列表*/
    private List<Permission> permissionList;
}
