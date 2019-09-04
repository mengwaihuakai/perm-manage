package com.solid.subscribe.web.perm.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Table(name="perm_user_role")
@Data
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

}
