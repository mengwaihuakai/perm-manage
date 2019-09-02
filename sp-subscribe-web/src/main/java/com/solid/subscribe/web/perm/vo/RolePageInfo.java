package com.solid.subscribe.web.perm.vo;

import com.solid.subscribe.web.perm.entity.Permission;

import java.sql.Timestamp;
import java.util.List;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}
