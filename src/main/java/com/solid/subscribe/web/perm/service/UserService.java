package com.solid.subscribe.web.perm.service;

import com.solid.subscribe.web.perm.dao.RoleMapper;
import com.solid.subscribe.web.perm.dao.UserMapper;
import com.solid.subscribe.web.perm.dao.UserRoleMapper;
import com.solid.subscribe.web.perm.entity.Role;
import com.solid.subscribe.web.perm.entity.User;
import com.solid.subscribe.web.perm.entity.UserRole;
import com.solid.subscribe.web.perm.util.*;
import com.solid.subscribe.web.perm.vo.UserPageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZOOMY on 2018/8/8.
 */
@Service
public class UserService {
    @Autowired(required = false)
    UserMapper userMapper;
    @Autowired(required = false)
    RoleMapper roleMapper;
    @Autowired(required = false)
    UserRoleMapper userRoleMapper;
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
                    //保存
                    moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                            LogConstants.Page.UPDATE_PWD,
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

    public ResultHandler saveUser(HttpServletRequest request, User user, String confirmPwd, String[] roleIdArr, ResultHandler resultHandler) throws Exception {
        User oldUser = userMapper.getUserByAccount(user.getAccount());
        if (oldUser != null) {
            resultHandler.setMessage("已有账号：" + user.getAccount() + ",  请输入新的账号");
        } else {
            if (!StringUtils.equals(user.getPassword(), confirmPwd)) {
                resultHandler.setMessage("两次输入密码不一致，请重新输入");
            } else {
                StringBuilder content = new StringBuilder("");//日志内容
                //1、保存user
                //加密处理(md5 2次加密，用账户名即第2个参数作为盐值)
                user.setPassword(new Md5Hash(user.getPassword(), user.getAccount(), 2).toString());
                Integer saveUserSuccess = userMapper.saveUser(user);
                if (saveUserSuccess > 0) {//保存用户成功
                    //2、保存userRole
                    List<UserRole> userRoleList = new LinkedList();
                    for (int i = 0; i < roleIdArr.length; i++) {
                        UserRole userRole = new UserRole();
                        userRole.setUserId(user.getId());
                        userRole.setRoleId(Integer.valueOf(roleIdArr[i]));
                        userRoleList.add(userRole);
                        content.append(roleIdArr[i]).append(",");
                    }
                    Integer saveUserRoleSuccess = userRoleMapper.saveUserRoleList(userRoleList);
                    //3、保存userRole日志
                    if (saveUserRoleSuccess > 0) {
                        content.insert(0, "创建了[用户角色表]条记录，用户id是，角色id是");
                        content.insert(19, user.getId());
                        content.insert(10, roleIdArr.length);
                        content.deleteCharAt(content.length() - 1);
                        moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                                LogConstants.Page.CREATE_USER,
                                LogConstants.PageUrl.CREATE_USER,
                                LogConstants.OperateType.ADD,
                                LogConstants.ObjectType.USER_ROLE,
                                user.getId() + "",
                                content.toString(),
                                request
                        );
                    }
                    //4、保存user日志信息
                    //获取content
                    CompareBeanUtils userCb = new CompareBeanUtils(User.class, user);
                    userCb.compare("id", "id");
                    userCb.compare("account", "账号");
                    userCb.compare("password", "密码");
                    userCb.compare("status", "状态");
                    if (StringUtils.isNotBlank(content)) {
                        content.append(";");
                    }
                    if (StringUtils.isNotBlank(userCb.toResult())) {
                        content.append("[用户表]").append(userCb.toResult());
                    }
                    //保存
                    moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                            LogConstants.Page.CREATE_USER,
                            LogConstants.PageUrl.CREATE_USER,
                            LogConstants.OperateType.ADD,
                            LogConstants.ObjectType.USER,
                            user.getId() + "",
                            content.toString(),
                            request
                    );
                }
                //5、组织返回信息
                resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
                resultHandler.setMessage("创建用户成功");
            }
        }
        return resultHandler;
    }

    public ResultHandler updateUser(HttpServletRequest request, User user, String confirmPwd, String[] roleIdArr, ResultHandler resultHandler) throws Exception {
        User oldUser = userMapper.getUserById(user.getId());
        if (null == oldUser) {
            resultHandler.setMessage("数据异常，联系管理员");
            return resultHandler;
        }
        if (!StringUtils.equals(oldUser.getAccount(), user.getAccount())) {//更改了账号信息
            if (StringUtils.equals(user.getAccount(), "admin")) {
                resultHandler.setMessage("不可更改管理员账号！");
                return resultHandler;
            }
            User userByAccount = userMapper.getUserByAccount(user.getAccount());
            if (null != userByAccount) {
                resultHandler.setMessage("已有账号：" + user.getAccount() + ",  请输入新的账号或原账号");
                return resultHandler;
            }
        }
        if (!StringUtils.equals(user.getPassword(), confirmPwd)) {
            resultHandler.setMessage("两次输入密码不一致，请重新输入");
            return resultHandler;
        }
        StringBuilder userRoleAddContent = new StringBuilder("");//userRole表添加记录时日志内容
        StringBuilder userUpdateContent = new StringBuilder("");//user表更新记录时日志内容
        StringBuilder usreRoleDeleteContent = new StringBuilder("");//userRole表删除记录时日志内容
        //1、更新userRole
        //（1）组织需要删除或添加的数据
        List<Integer> deleteList = userRoleMapper.getRoleIdByUserId(user.getId());//需要删除---从数据库中获取原有的，再去除与前端传入重合的（remainList）
        List<UserRole> addList = new LinkedList<>();//需要添加
        Boolean deleteListContainNewRoleId = false;
        Integer remainRoleId = -1;
        List<Integer> remainList = new LinkedList<>();//数据库中获取的与前端传入重合的  即不需要做删除的 即保留不操作的
        for (String newRoleId : roleIdArr) {
            //1）需要删除的
            for (int i = 0; i < deleteList.size(); i++) {
                if (deleteList.get(i).equals(Integer.valueOf(newRoleId))) {
                    deleteListContainNewRoleId = true;
                    remainRoleId = deleteList.get(i);
                }
            }
            if (deleteListContainNewRoleId) {
                remainList.add(remainRoleId);
                //2）需要添加的
            } else {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(Integer.valueOf(newRoleId));
                userRoleAddContent.append(newRoleId).append(",");
                addList.add(userRole);
            }
            //3）将参数还原
            deleteListContainNewRoleId = false;
            remainRoleId = -1;
        }
        //1）需要删除的
        deleteList.removeAll(remainList);//从数据库中获取原有的，再去除与前端传入重合的
        //（2）删除数据
        if (null != deleteList && deleteList.size() > 0) {
            //1) 删除
            Map<String, Object> deleteParam = new HashMap<>();
            deleteParam.put("userId", user.getId());
            deleteParam.put("deleteList", deleteList);
            Integer deleteSusscee = userRoleMapper.deleteByRoleIdList(deleteParam);
            //2）日志
            if (deleteSusscee > 0) {
                //保存到日志
                //查询出要删除的roleId
                for (Integer deleteId : deleteList) {
                    usreRoleDeleteContent.append(deleteId).append(",");
                }
                usreRoleDeleteContent.deleteCharAt(usreRoleDeleteContent.length() - 1);//删除结尾逗号
                usreRoleDeleteContent.insert(0, "删除了[用户角色表]条数据,用户id是，角色id是");
                usreRoleDeleteContent.insert(19, user.getId());
                usreRoleDeleteContent.insert(10, deleteList.size());
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.EDIT_USER,
                        LogConstants.PageUrl.EDIT_USER,
                        LogConstants.OperateType.DELETE,
                        LogConstants.ObjectType.USER_ROLE,
                        user.getId() + "",
                        usreRoleDeleteContent.toString(),
                        request);
                //拼接user表更新数据日志内容
                userUpdateContent.append(usreRoleDeleteContent);
            }
        }
        //（3）批量添加新数据
        if (null != addList && addList.size() > 0) {
            //1）添加
            Integer saveUserRoleSuccess = userRoleMapper.saveUserRoleList(addList);
            //2）日志
            if (saveUserRoleSuccess > 0) {
                userRoleAddContent.insert(0, "创建了[用户角色表]条记录，用户id是，角色id是");
                userRoleAddContent.insert(19, user.getId());
                userRoleAddContent.insert(10, addList.size());
                userRoleAddContent.deleteCharAt(userRoleAddContent.length() - 1);//删除结尾逗号
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.EDIT_USER,
                        LogConstants.PageUrl.EDIT_USER,
                        LogConstants.OperateType.ADD,
                        LogConstants.ObjectType.USER_ROLE,
                        user.getId() + "",
                        userRoleAddContent.toString(),
                        request
                );
                //拼接user表更新数据日志内特
                if (StringUtils.isNotBlank(userUpdateContent)) {
                    userUpdateContent.append(";");
                }
                userUpdateContent.append(userRoleAddContent);
            }
        }
        //2、更新user
        //（1）
        if (!StringUtils.equals(user.getPassword(), oldUser.getPassword())) {//用户对密码做了修改
            //加密处理(md5 2次加密，用账户名作为盐值)
            user.setPassword(new Md5Hash(user.getPassword(), user.getAccount(), 2).toString());
        }
        Integer updateUserSuccess = userMapper.updateUser(user);
        //（2）添加到日志信息
        if (updateUserSuccess > 0) {
            //获取content
            CompareBeanUtils userCb = new CompareBeanUtils(User.class, oldUser, user);
            userCb.compare("id", "id");
            userCb.compare("account", "账号");
            userCb.compare("password", "密码");
            userCb.compare("status", "状态");
            if (StringUtils.isNotBlank(userUpdateContent)) {
                userUpdateContent.append(";");
            }
            if (StringUtils.isNotBlank(userCb.toResult().toString())) {
                userUpdateContent.append("[用户表]").append(userCb.toResult());
            }
            //保存
            moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                    LogConstants.Page.EDIT_USER,
                    LogConstants.PageUrl.EDIT_USER,
                    LogConstants.OperateType.UPDATE,
                    LogConstants.ObjectType.USER,
                    user.getId() + "",
                    userUpdateContent.toString(),
                    request
            );
        }
        //3、组织返回信息
        resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
        resultHandler.setMessage("修改用户信息成功");
        return resultHandler;
    }

    public UserPageInfo getUserById(Integer id) throws Exception {
        List<Role> roleValidList = roleMapper.getValidRoles();
        List<UserRole> userRoleValidList = userRoleMapper.getUserRoles();
        User user = userMapper.getUserById(id);
        UserPageInfo userPageInfo = new UserPageInfo();
        List<Role> roleList = new LinkedList<>();
        userPageInfo.setId(user.getId());
        userPageInfo.setAccount(user.getAccount());
        userPageInfo.setPassword(user.getPassword());
        userPageInfo.setStatus(user.getStatus());
        userPageInfo.setLastLoginIp(user.getLastLoginIp());
        userPageInfo.setLastLoginTime(user.getLastLoginTime());
        userPageInfo.setCreateTime(user.getCreateTime());
        userPageInfo.setUpdateTime(user.getUpdateTime());
        for (UserRole userRole : userRoleValidList) {//用户表id 与 用户角色表的userId对应；用户角色表的roleId 与 角色表的id对应
            if (user.getId() == userRole.getUserId()) {
                for (Role role : roleValidList) {
                    if (userRole.getRoleId() == role.getId()) {
                        roleList.add(role);
                    }
                }
            }
        }
        userPageInfo.setRoleList(roleList);
        return userPageInfo;
    }

    public List<UserPageInfo> getUserPageInfo() throws Exception {
        List<UserPageInfo> userPageInfoList = new LinkedList<>();
        //组装页面用户数据
        List<User> userList = userMapper.getUsers();
        List<Role> roleValidList = roleMapper.getValidRoles();
        List<UserRole> userRoleValidList = userRoleMapper.getUserRoles();
        for (User user : userList) {
            UserPageInfo userPageInfo = new UserPageInfo();
            List<Role> roleList = new LinkedList<>();
            userPageInfo.setId(user.getId());
            userPageInfo.setAccount(user.getAccount());
            userPageInfo.setPassword(user.getPassword());
            userPageInfo.setStatus(user.getStatus());
            userPageInfo.setLastLoginIp(user.getLastLoginIp());
            userPageInfo.setLastLoginTime(user.getLastLoginTime());
            userPageInfo.setCreateTime(user.getCreateTime());
            userPageInfo.setUpdateTime(user.getUpdateTime());
            for (UserRole userRole : userRoleValidList) {//用户表id 与 用户角色表的userId对应；用户角色表的roleId 与 角色表的id对应
                if (user.getId() == userRole.getUserId()) {
                    for (Role role : roleValidList) {
                        if (userRole.getRoleId() == role.getId()) {
                            roleList.add(role);
                        }
                    }
                }
            }
            userPageInfo.setRoleList(roleList);
            userPageInfoList.add(userPageInfo);
        }
        return userPageInfoList;
    }

    public void deleteUser(Integer userId, HttpServletRequest request) throws Exception {
        StringBuilder content = new StringBuilder("");
        //1.用户角色表
        //（1）查询出要删除的roleId
        List<Integer> roleIdList = userRoleMapper.getRoleIdByUserId(userId);
        if (null != roleIdList && roleIdList.size() > 0) {
            //（2）删除数据
            Integer userRoleSuccess = userRoleMapper.deleteByUserId(userId);
            //（3）日志保存
            if (userRoleSuccess > 0) {
                for (Integer roleId : roleIdList) {
                    content.append(roleId).append(",");
                }
                content.deleteCharAt(content.length() - 1);
                content.insert(0, "删除了[用户角色表]条数据,用户id是，角色id是");
                content.insert(19, userId);
                content.insert(10, roleIdList.size());
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.PERM_USER,
                        LogConstants.PageUrl.DELETE_USER,
                        LogConstants.OperateType.DELETE,
                        LogConstants.ObjectType.USER_ROLE,
                        userId.toString(),
                        content.toString(),
                        request);
            }
        }
        //2.用户表
        //（1）删除数据
        Integer userSuccess = userMapper.deleteById(userId);
        //（2）保存到日志
        if (userSuccess > 0) {
            if (StringUtils.isNotBlank(content)) {
                content.append(";");
            }
            content.append("删除了[用户表]一条数据," + "用户id:" + userId);
            moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                    LogConstants.Page.PERM_USER,
                    LogConstants.PageUrl.DELETE_USER,
                    LogConstants.OperateType.DELETE,
                    LogConstants.ObjectType.USER,
                    userId.toString(),
                    content.toString(),
                    request);
        }
    }
}
