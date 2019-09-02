package com.solid.subscribe.web.perm.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Table(name="perm_role_permission")
public class RolePermission {
    /*角色ID*/
    @Id
    @NotNull(message="角色Id不可以为空")
    private int roleId;
    /*权限ID*/
    @Id
    @NotNull(message="权限Id不可以为空")
    private int permissionId;
    /*创建时间*/
    private Timestamp createTime;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "roleId=" + roleId +
                ", permissionId=" + permissionId +
                ", createTime=" + createTime +
                '}';
    }
}
