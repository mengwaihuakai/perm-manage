package com.solid.subscribe.web.perm.vo;

import com.solid.subscribe.web.perm.entity.Role;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
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

}
