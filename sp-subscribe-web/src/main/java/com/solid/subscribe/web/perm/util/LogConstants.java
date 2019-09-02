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
        public static final String UPDATE="修改记录";//修改记录
        public static final String VISIT="访问页面";//修改记录
    }



    public class PageUrl{
        public static final String LOGIN="login";//登陆
        public static final String UPDATE_PWD="perm/user/toUpdatePwd";//密码更新
        public static final String DO_UPDATE_PWD="perm/user/updatePwd";//修改密码
    }

    public class Page{
        public static final String LOGIN="登陆页";
        public static final String UPDATE_PWD="更新密码";

    }

    public class ObjectType{
        public static final String USER="perm_user";//用户表
    }

}
