package com.solid.subscribe.web.perm.entity;


import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Table(name="perm_permission")
public class Permission implements Serializable {
    /*session序列化 反序列化*/
    private static final long serialVersionUID = 1L;
    /*权限ID*/
    @Id
    private int id;
    /*权限名称*/
    @NotBlank(message="权限名称不为空")
    @Length(min=1,max=200,message="权限名称长度不超过200")
    private String name;
    /*权限代码*/
    @NotBlank(message="权限代码不为空")
    @Length(min=1,max=100,message="权限代码长度不超过100")
    private String code;
    /*权限状态*/
    @NotNull(message="权限状态不为空")
    @Min(value=0,message="权限状态不合法")
    @Max(value=2,message="权限状态不合法")
    private int status;
    /*页面名称*/
    @NotBlank(message="父级菜单代码不为空")
    @Length(min=1,max=100,message="父级菜单代码不超过100")
    @Column(name="menu_code")
    private String menuCode;
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

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
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
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", status='" + status + '\'' +
                ", menuCode='" + menuCode + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
