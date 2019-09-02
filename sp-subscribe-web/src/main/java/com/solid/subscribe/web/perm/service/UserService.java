package com.solid.subscribe.web.perm.service;

import com.solid.subscribe.web.perm.dao.RoleMapper;
import com.solid.subscribe.web.perm.dao.UserMapper;
import com.solid.subscribe.web.perm.entity.User;
import com.solid.subscribe.web.perm.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/8.
 */
@Service
public class UserService {
    @Autowired(required = false)
    UserMapper userMapper;
    @Autowired(required = false)
    RoleMapper roleMapper;
    @Autowired
    MoniLogService moniLogService;

    //装填登录时间和登录IP
    public void saveLoginIpAndTime(HttpServletRequest request, User user, ResultHandler resultHandler) throws Exception {
        //保留旧数据
        User oldUser = new User();
        CombineBeans combine = new CombineBeans();
        combine.combine(user, oldUser);
        //装填新数据
        user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
        user.setLastLoginIp(InternetProtocol.getRemoteAddr(request));
        user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        Integer i = userMapper.updateUser(user);//更新用户信息
        if (i > 0) {//添加日志信息
            resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
            /*添加到日志*/
            //获取content
            CompareBeanUtils cb = new CompareBeanUtils(User.class, oldUser, user);
            cb.compare("lastLoginTime", "上次登陆时间");
            cb.compare("lastLoginIp", "上次登陆ip");
            //保存
            moniLogService.saveMonitor(LogConstants.LogType.LOGIN,
                    LogConstants.Page.LOGIN,
                    LogConstants.PageUrl.LOGIN,
                    LogConstants.OperateType.UPDATE,
                    LogConstants.ObjectType.USER,
                    user.getId() + "",
                    cb.toResult(),
                    request
            );

        } else {
            resultHandler.setMessage("Loading login time and login IP failed");
        }
    }

    public ResultHandler updatePwd(HttpServletRequest request, String account, String pwd, String confirmPwd, ResultHandler resultHandler) throws Exception {
        Integer i = 0;
        User oldUser = userMapper.getUserByAccount(account);
        if (null == oldUser) {
            resultHandler.setMessage("Account does not exist");
        } else {
            User user = new User();
            CombineBeans combine = new CombineBeans();
            combine.combine(oldUser, user);
            //验证两次输入密码一致性
            if (!StringUtils.equals(pwd, confirmPwd)) {
                resultHandler.setMessage("The two passwords are inconsistent, please re-enter");
            } else {//密码验证通过
                user.setPassword(new Md5Hash(pwd, account, 2).toString());//加密处理(md5 2次加密，用账户名作为盐值)
                i = userMapper.updateUser(user);//更新数据
                if (i <= 0) {
                    resultHandler.setMessage("Failed to  modify password");
                } else {
                    resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
                    resultHandler.setMessage("Success");
                    /*添加日志信息*/
                    //获取content
                    CompareBeanUtils cb = new CompareBeanUtils(User.class, oldUser, user);
                    cb.compare("password", "密码");
                    //判断是修改密码还是忘记密码页面
                    String page = LogConstants.Page.UPDATE_PWD;
                    //保存
                    moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                            page,
                            LogConstants.PageUrl.DO_UPDATE_PWD,
                            LogConstants.OperateType.UPDATE,
                            LogConstants.ObjectType.USER,
                            user.getId() + "",
                            cb.toResult(),
                            request
                    );
                }
            }
        }
        return resultHandler;
    }

}
