package com.solid.subscribe.web.perm.util.shiro;

import com.solid.subscribe.web.perm.dao.PermissionMapper;
import com.solid.subscribe.web.perm.dao.RoleMapper;
import com.solid.subscribe.web.perm.dao.UserMapper;
import com.solid.subscribe.web.perm.entity.Permission;
import com.solid.subscribe.web.perm.entity.Role;
import com.solid.subscribe.web.perm.entity.User;
import com.solid.subscribe.web.perm.util.Constants;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * Created by ZOOMY on 2018/8/20.
 */
public class MyShiroRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory
            .getLogger(MyShiroRealm.class);
    @Autowired(required = false)
    RoleMapper roleMapper;
    @Autowired(required = false)
    PermissionMapper permissionMapper;
    @Autowired(required = false)
    UserMapper userMapper;

    @Override
    /*授予权限和角色*/
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //权限配置-->MyShiroRealm.doGetAuthorizationInfo()
        // 添加权限 和 角色信息
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 获取当前登陆用户
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //保存角色
        Set<String> roles = roleMapper.getRoleCodesByUserId(user.getId());
        if (null != roles && roles.size() > 0) {
            for (String code : roles) {
                authorizationInfo.addRole(code);
            }
        }
        // 权限数据
        Set<String> perms = userMapper.getPermCodesByUserId(user.getId());
        if (null != perms && perms.size() > 0) {
            // 授权角色下所有权限
            for (String code : perms) {
                authorizationInfo.addStringPermission(code);
            }
        }
        return authorizationInfo;
    }

    /*登陆认证*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //UsernamePasswordToken用于存放提交的登录信息
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        logger.info("用户登录认证：验证当前Subject时获取到token为：" + ReflectionToStringBuilder
                .toString(token, ToStringStyle.MULTI_LINE_STYLE));
        String account = token.getUsername();
        // 调用数据层
        User user = userMapper.getUserByAccount(account);
        logger.debug("用户登录认证！用户信息user：" + user);
        if (user == null) {
            // 用户不存在
            throw new UnknownAccountException();
        } else if (null != user && (user.getStatus() == (Constants.Status.LOCKED))) {
            throw new LockedAccountException();
        } else if (null != user && (user.getStatus() == Constants.Status.INVALID)) {
            throw new DisabledAccountException();
        } else if (null != user && (user.getStatus() == Constants.Status.VALID)) {
            // 密码存在
            // 第一个参数 ，登陆后，需要在session保存数据
            // 第二个参数，查询到密码(加密规则要和自定义的HashedCredentialsMatcher中的HashAlgorithmName散列算法一致)
            // 第三个参数，加密的盐（用账户名作为盐值）
            // 第四个参数 ，realm名字
            return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getAccount()), getName());
        }
        return null;
    }
}