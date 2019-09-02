package com.solid.subscribe.web.perm.entity;

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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", lastLoginTime=" + lastLoginTime +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getId() != user.getId()) return false;
        if (getStatus() != user.getStatus()) return false;
        if (getAccount() != null ? !getAccount().equals(user.getAccount()) : user.getAccount() != null) return false;
        if (getPassword() != null ? !getPassword().equals(user.getPassword()) : user.getPassword() != null)
            return false;
        if (getLastLoginTime() != null ? !getLastLoginTime().equals(user.getLastLoginTime()) : user.getLastLoginTime() != null)
            return false;
        if (getLastLoginIp() != null ? !getLastLoginIp().equals(user.getLastLoginIp()) : user.getLastLoginIp() != null)
            return false;
        if (getCreateTime() != null ? !getCreateTime().equals(user.getCreateTime()) : user.getCreateTime() != null)
            return false;
        return getUpdateTime() != null ? getUpdateTime().equals(user.getUpdateTime()) : user.getUpdateTime() == null;
    }
}
