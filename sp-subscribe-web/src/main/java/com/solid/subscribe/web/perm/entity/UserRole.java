package com.solid.subscribe.web.perm.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Table(name="perm_user_role")
public class UserRole {
    /*用户ID*/
    @Id
    @NotNull(message="用户Id不可以为空")
    private int userId;
    /*权限ID*/
    @Id
    @NotNull(message="角色Id不可以为空")
    private int roleId;
    /*创建时间*/
    private Timestamp createTime;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole)) return false;

        UserRole userRole = (UserRole) o;

        if (getUserId() != userRole.getUserId()) return false;
        return getRoleId() == userRole.getRoleId();
    }

    @Override
    public int hashCode() {
        int result = getUserId();
        result = 31 * result + getRoleId();
        return result;
    }
}
