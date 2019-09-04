package com.solid.subscribe.web.perm.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/8.
 */
@Table(name="perm_user")
@Data
public class User implements Serializable {
    /*session序列化 反序列化*/
    private static final long serialVersionUID = 1L;
    /*用户ID*/
    @Id
    private int id;
    /*账户*/
    @NotBlank(message="账户不可以为空")
    @Length(min=1,max=50,message="账户名长度不可以超过50")
    private String account;
    /*密码*/
    @NotBlank(message="密码不可以为空")
    @Length(min=1,max=255,message="密码长度不可以超过255")
    private String password;
    /*状态*/
    @NotNull(message="状态不可以为空")
    @Min(value=0,message="用户状态不合法")
    @Max(value=2,message="用户状态不合法")
    private int status;
    /*上次登陆时间*/
    @Column(name="last_login_time")
    private Timestamp lastLoginTime;
    /*上次登陆地址*/
    @Column(name="last_login_ip")
    private String lastLoginIp;
    /*创建时间*/
    @Column(name="create_time")
    private Timestamp createTime;
    /*更新时间*/
    @Column(name="update_time")
    private Timestamp updateTime;

}
