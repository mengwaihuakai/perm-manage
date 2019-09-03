package com.solid.subscribe.web.perm.vo;

import com.solid.subscribe.web.perm.entity.Role;

import java.sql.Timestamp;
import java.util.List;

public class UserPageInfo {
    /*用户ID*/
    private int id;
    /*账户*/
    private String account;
    /*密码*/
    private String password;
    /*状态*/
    private int status;
    /*上次登陆时间*/
    private Timestamp lastLoginTime;
    /*上次登陆地址*/
    private String lastLoginIp;
    /*创建时间*/
    private Timestamp createTime;
    /*更新时间*/
    private Timestamp updateTime;
    /*角色列表*/
    private List<Role> roleList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
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

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
