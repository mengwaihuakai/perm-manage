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
 * Created by ZOOMY on 2018/8/20.
 */
@Table(name="perm_role")
@Data
public class Role implements Serializable {
    /*session序列化 反序列化*/
    private static final long serialVersionUID = 1L;
    /*角色ID*/
    @Id
    private int id;
    /*角色名称*/
    @NotBlank(message="角色名称不为空")
    @Length(min=1,max=100,message="角色名称长度不超过100")
    private String name;
    /*角色状态*/
    @NotNull(message="角色状态不为空")
    @Min(value=0,message="角色状态不合法")
    @Max(value=2,message="角色状态不合法")
    private int status;
    /*创建时间*/
    @Column(name="create_time")
    private Timestamp createTime;
    /*更新时间*/
    @Column(name="update_time")
    private Timestamp updateTime;
    /*角色代码*/
    @NotBlank(message="角色代码不为空")
    @Length(min=1,max=100,message="角色代码长度不超过100")
    private String code;


}
