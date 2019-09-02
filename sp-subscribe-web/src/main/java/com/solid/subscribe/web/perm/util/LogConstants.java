package com.solid.subscribe.web.perm.util;

/**
 * Created by ZOOMY on 2018/8/29.
 */
public class LogConstants {
    public class LogType{
        public static final String LOGIN="登陆日志";//登陆
        public static final String OPERATE="操作日志";//操作
        public static final String VISIT="访问日志";//访问
    }

    public class OperateType{
        public static final String ADD="添加记录";//添加记录
        public static final String DELETE="删除记录";//删除记录
        public static final String UPDATE="修改记录";//修改记录
        public static final String VISIT="访问页面";//修改记录
    }



    public class PageUrl{
        public static final String LOGIN="login";//登陆
        public static final String FORGET_PWD="perm/user/toForgetPwd";//忘记密码修改
        public static final String UPDATE_PWD="perm/user/toUpdatePwd";//密码更新
        public static final String DO_UPDATE_PWD="perm/user/updatePwd";//修改密码
        public static final String TO_CREATE_USER="perm/user/toAddUser";//请求创建用户页面
        public static final String CREATE_USER="perm/user/addUser";//创建用户
        public static final String TO_EDIT_USER="perm/user/toEditUser";//请求编辑用户页面
        public static final String EDIT_USER="perm/user/editUser";//编辑用户
        public static final String DELETE_USER="perm/user/deleteUser";//删除用户
        public static final String PERM_USER="perm/user/permUser";//用户管理
        public static final String USER_LOG="perm/user/log";//用户页日志
        public static final String TO_CREATE_ROLE="perm/role/toRoleUser";//请求创建角色页面
        public static final String CREATE_ROLE="perm/role/addRole";//创建用户
        public static final String TO_EDIT_ROLE="perm/role/toEditRole";//请求编辑用户页面
        public static final String EDIT_ROLE="perm/role/editRole";//编辑用户
        public static final String DELETE_ROLE="perm/role/deleteRole";//删除用户
        public static final String PERM_ROLE="perm/role/permRole";//用户管理
        public static final String ROLE_LOG="perm/role/log";//用户页日志
        public static final String LOG_SUMMARY="log/logSummary";//日志汇总
    }

    public class Page{
        public static final String LOGIN="登陆页";
        public static final String FORGET_PWD="忘记密码";
        public static final String UPDATE_PWD="更新密码";
        public static final String CREATE_USER="创建用户";
        public static final String EDIT_USER="编辑用户";
        public static final String PERM_USER="用户管理";
        public static final String USER_LOG="用户页日志";
        public static final String CREATE_ROLE="创建角色";
        public static final String EDIT_ROLE="编辑角色";
        public static final String PERM_ROLE="角色管理";
        public static final String ROLE_LOG="角色页日志";
        public static final String LOG_SUMMARY="日志汇总";

    }

    public class ObjectType{
        public static final String USER="perm_user";//用户表
        public static final String ROLE="perm_role";//角色表
        public static final String PERMISSION="perm_permission";//权限表
        public static final String USER_ROLE="perm_user_role";//用户角色表
        public static final String ROLE_PERMISSION="perm_role_permission";//角色权限表
        public static final String MONITOR_LOG="monitor_log";
    }

}
