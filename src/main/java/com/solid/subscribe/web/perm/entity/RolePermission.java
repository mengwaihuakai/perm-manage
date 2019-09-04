package com.solid.subscribe.web.perm.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Table(name="perm_role_permission")
@Data
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

}
