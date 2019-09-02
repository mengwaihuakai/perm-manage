package com.solid.subscribe.web.perm.util;

/**
 * Created by ZOOMY on 2018/8/22.
 */
public class Constants {

    public class Result {
        public static final int RESULTCODE_SUCCESS = 0;//处理成功
        public static final int RESULTCODE_FAILURE = 1;//处理失败
    }

    public class Status {
        public static final int VALID = 0;//有效
        public static final int INVALID = 1;//无效
        public static final int LOCKED = 2;//冻结
    }

    public class Role {
        public static final String INTERNAL_USER = "internal_user";//外部用户
    }

}
