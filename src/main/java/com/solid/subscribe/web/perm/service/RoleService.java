package com.solid.subscribe.web.perm.service;

import com.solid.subscribe.web.perm.dao.PermissionMapper;
import com.solid.subscribe.web.perm.dao.RoleMapper;
import com.solid.subscribe.web.perm.dao.RolePermissionMapper;
import com.solid.subscribe.web.perm.dao.UserRoleMapper;
import com.solid.subscribe.web.perm.entity.Permission;
import com.solid.subscribe.web.perm.entity.Role;
import com.solid.subscribe.web.perm.entity.RolePermission;
import com.solid.subscribe.web.perm.util.CompareBeanUtils;
import com.solid.subscribe.web.perm.util.Constants;
import com.solid.subscribe.web.perm.util.LogConstants;
import com.solid.subscribe.web.perm.util.ResultHandler;
import com.solid.subscribe.web.perm.vo.RolePageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Service
public class RoleService {
    @Autowired(required = false)
    RoleMapper roleMapper;
    @Autowired(required = false)
    PermissionMapper permissionMapper;
    @Autowired(required = false)
    UserRoleMapper userRoleMapper;
    @Autowired(required = false)
    RolePermissionMapper rolePermissionMapper;
    @Autowired
    MoniLogService moniLogService;

    public List<Role> getValidRoles() throws Exception {
        return roleMapper.getValidRoles();
    }


    public List<RolePageInfo> getRolePageInfo(Role roleParam, HttpServletRequest request) throws Exception {
        List<RolePageInfo> rolePageInfoList = new LinkedList<>();
        //组装页面用户数据
        List<Role> roleList = roleMapper.getRoles(roleParam);
        List<Permission> permissionValidList = permissionMapper.getValidPermissions();
        List<RolePermission> rolePermValidList = rolePermissionMapper.getRolePerms();
        for (Role role : roleList) {
            RolePageInfo rolePageInfo = new RolePageInfo();
            List<Permission> returnPermList = new LinkedList();
            rolePageInfo.setId(role.getId());
            rolePageInfo.setName(role.getName());
            rolePageInfo.setCode(role.getCode());
            rolePageInfo.setStatus(role.getStatus());
            rolePageInfo.setCreateTime(role.getCreateTime());
            rolePageInfo.setUpdateTime(role.getUpdateTime());
            for (RolePermission rolePerm : rolePermValidList) {//角色表id 与 角色权限表的roleId对应；角色权限表的permissionId 与 权限表的id对应
                if (role.getId() == rolePerm.getRoleId()) {
                    for (Permission permission : permissionValidList) {
                        if (rolePerm.getPermissionId() == permission.getId()) {
                            returnPermList.add(permission);
                        }
                    }
                }
            }
            rolePageInfo.setPermissionList(returnPermList);
            rolePageInfoList.add(rolePageInfo);
        }
        return rolePageInfoList;
    }


    public void deleteRole(Integer roleId, HttpServletRequest request) throws Exception {
        StringBuilder content = new StringBuilder("");
        //1.用户角色表
        //（1）查询出要删除的userId
        List<Integer> userIdList = userRoleMapper.getUserIdByRoleId(roleId);
        if (null != userIdList && userIdList.size() > 0) {
            //（2）删除数据
            Integer userRoleSuccess = userRoleMapper.deleteByRoleId(roleId);
            //（3）日志保存
            if (userRoleSuccess > 0) {
                for (Integer userId : userIdList) {
                    content.append(userId).append(",");
                }
                content.deleteCharAt(content.length() - 1);
                content.insert(0, "删除了[用户角色表]条数据,角色id是，用户id是");
                content.insert(19, roleId);
                content.insert(10, userIdList.size());
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.PERM_USER,
                        LogConstants.PageUrl.DELETE_USER,
                        LogConstants.OperateType.DELETE,
                        LogConstants.ObjectType.USER_ROLE,
                        roleId.toString(),
                        content.toString(),
                        request);
            }
        }
        //2.角色权限表
        //（1）查询出要删除的permissionId
        List<Integer> permissionIdList = rolePermissionMapper.getPermIdByRoleId(roleId);
        if (null != permissionIdList && permissionIdList.size() > 0) {
            //（2）删除数据
            Integer userRoleSuccess = rolePermissionMapper.deleteByRoleId(roleId);
            //（3）日志保存
            if (userRoleSuccess > 0) {
                for (Integer permissionId : permissionIdList) {
                    content.append(permissionId).append(",");
                }
                content.deleteCharAt(content.length() - 1);
                content.insert(0, "删除了[角色权限表]条数据,角色id是，权限id是");
                content.insert(19, roleId);
                content.insert(10, permissionIdList.size());
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.PERM_ROLE,
                        LogConstants.PageUrl.DELETE_ROLE,
                        LogConstants.OperateType.DELETE,
                        LogConstants.ObjectType.ROLE_PERMISSION,
                        roleId.toString(),
                        content.toString(),
                        request);
            }
        }
        //3.角色表
        //（1）删除数据
        Integer userSuccess = roleMapper.deleteById(roleId);
        //（2）保存到日志
        if (userSuccess > 0) {
            if (StringUtils.isNotBlank(content)) {
                content.append(";");
            }
            content.append("删除了[角色表]一条数据," + "用户id:" + roleId);
            moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                    LogConstants.Page.PERM_ROLE,
                    LogConstants.PageUrl.DELETE_ROLE,
                    LogConstants.OperateType.DELETE,
                    LogConstants.ObjectType.ROLE,
                    roleId.toString(),
                    content.toString(),
                    request);
        }
    }


    public ResultHandler saveRole(HttpServletRequest request, Role role, String[] permissionIdArr, ResultHandler resultHandler) throws Exception {
        Role oldRole = roleMapper.getRoleByName(role.getName());
        if (oldRole != null) {
            resultHandler.setMessage("已有角色：" + role.getName() + ",  请输入新的账号");
        } else {
            StringBuilder content = new StringBuilder("");//日志内容
            //1、保存role
            Integer saveRoleSuccess = roleMapper.saveRole(role);
            if (saveRoleSuccess > 0) {//保存角色成功
                //2、保存rolePermission
                List<RolePermission> rolePermList = new LinkedList<>();
                int count = 0;
                for (int i = 0; i < permissionIdArr.length; i++) {
                    if (StringUtils.isNotBlank(permissionIdArr[i])) {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRoleId(role.getId());
                        rolePermission.setPermissionId(Integer.valueOf(permissionIdArr[i]));
                        rolePermList.add(rolePermission);
                        content.append(permissionIdArr[i]).append(",");
                        count++;
                    }
                }
                Integer saveRolePermSuccess = rolePermissionMapper.saveRolePermList(rolePermList);
                //3、保存rolePermission日志
                if (saveRolePermSuccess > 0) {
                    content.insert(0, "创建了[角色权限表]条记录，角色id是，权限id是");
                    content.insert(19, role.getId());
                    content.insert(10, count);
                    content.deleteCharAt(content.length() - 1);
                    moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                            LogConstants.Page.CREATE_ROLE,
                            LogConstants.PageUrl.CREATE_ROLE,
                            LogConstants.OperateType.ADD,
                            LogConstants.ObjectType.ROLE_PERMISSION,
                            role.getId() + "",
                            content.toString(),
                            request
                    );
                }
                //4、保存role日志信息
                //获取content
                CompareBeanUtils roleCb = new CompareBeanUtils(Role.class, role);
                roleCb.compare("id", "id");
                roleCb.compare("name", "姓名");
                roleCb.compare("status", "状态");
                if (StringUtils.isNotBlank(content)) {
                    content.append(";");
                }
                if (StringUtils.isNotBlank(roleCb.toResult())) {
                    content.append("[角色表]").append(roleCb.toResult());
                }
                //保存
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.CREATE_ROLE,
                        LogConstants.PageUrl.CREATE_ROLE,
                        LogConstants.OperateType.ADD,
                        LogConstants.ObjectType.ROLE,
                        role.getId() + "",
                        content.toString(),
                        request
                );
            }
            //5、组织返回信息
            resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
            resultHandler.setMessage("创建角色成功");
        }
        return resultHandler;
    }


    public ResultHandler updateRole(HttpServletRequest request, Role role, String[] permissionIdArr, ResultHandler resultHandler) throws Exception {
        Role oldRole = roleMapper.getRoleById(role.getId());
        if (null == oldRole) {
            resultHandler.setMessage("数据异常，联系管理员");
            return resultHandler;
        }
        if (!StringUtils.equals(oldRole.getName(), role.getName())) {//更改了角色名称
            Role roleByName = roleMapper.getRoleByName(role.getName());
            if (null != roleByName) {
                resultHandler.setMessage("已有名称：" + role.getName() + ",  请输入新的名称或原名称");
                return resultHandler;
            }
        }
        StringBuilder rolePermAddContent = new StringBuilder("");//rolePermission表添加记录时日志内容
        StringBuilder roleUpdateContent = new StringBuilder("");//role表更新记录时日志内容
        StringBuilder rolePermDeleteContent = new StringBuilder("");//userRole表删除记录时日志内容
        //1、更新rolePerm
        //（1）组织需要删除或添加的数据
        List<Integer> deleteList = rolePermissionMapper.getPermIdByRoleId(role.getId());//需要删除---从数据库中获取原有的，再去除与前端传入重合的（remainList）
        List<RolePermission> addList = new LinkedList<>();//需要添加
        Boolean deleteListContainNewPermId = false;
        Integer remainPermId = -1;
        List<Integer> remainList = new LinkedList<>();//数据库中获取的与前端传入重合的  即不需要做删除的 即保留不操作的
        for (String newPermId : permissionIdArr) {
            if (StringUtils.isNotBlank(newPermId)) {
                //1）需要删除的
                for (int i = 0; i < deleteList.size(); i++) {
                    if (deleteList.get(i).equals(Integer.valueOf(newPermId))) {
                        deleteListContainNewPermId = true;
                        remainPermId = deleteList.get(i);
                    }
                }
                if (deleteListContainNewPermId) {
                    remainList.add(remainPermId);
                    //2）需要添加的
                } else {
                    RolePermission rolePerm = new RolePermission();
                    rolePerm.setRoleId(role.getId());
                    rolePerm.setPermissionId(Integer.valueOf(newPermId));
                    rolePermAddContent.append(newPermId).append(",");
                    addList.add(rolePerm);
                }
                //3）将参数还原
                deleteListContainNewPermId = false;
                remainPermId = -1;
            }
        }
        //1）需要删除的
        deleteList.removeAll(remainList);//从数据库中获取原有的，再去除与前端传入重合的
        //（2）删除数据
        if (null != deleteList && deleteList.size() > 0) {
            //1) 删除
            Map<String, Object> deleteParam = new HashMap<>();
            deleteParam.put("roleId", role.getId());
            deleteParam.put("deleteList", deleteList);
            Integer deleteSusscee = rolePermissionMapper.deleteByPermIdList(deleteParam);
            //2）日志
            if (deleteSusscee > 0) {
                //保存到日志
                //查询出要删除的roleId
                for (Integer deleteId : deleteList) {
                    rolePermDeleteContent.append(deleteId).append(",");
                }
                rolePermDeleteContent.deleteCharAt(rolePermDeleteContent.length() - 1);//删除结尾逗号
                rolePermDeleteContent.insert(0, "删除了[角色权限表]条数据,角色id是，权限id是");
                rolePermDeleteContent.insert(19, role.getId());
                rolePermDeleteContent.insert(10, deleteList.size());
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.EDIT_ROLE,
                        LogConstants.PageUrl.EDIT_ROLE,
                        LogConstants.OperateType.DELETE,
                        LogConstants.ObjectType.ROLE_PERMISSION,
                        role.getId() + "",
                        rolePermDeleteContent.toString(),
                        request);
                //拼接user表更新数据日志内特
                roleUpdateContent.append(rolePermDeleteContent);
            }
        }
        //（3）批量添加新数据
        if (null != addList && addList.size() > 0) {
            //1）添加
            Integer saveUserRoleSuccess = rolePermissionMapper.saveRolePermList(addList);
            //2）日志
            if (saveUserRoleSuccess > 0) {
                rolePermAddContent.insert(0, "创建了[角色权限表]条记录，角色id是，权限id是");
                rolePermAddContent.insert(19, role.getId());
                rolePermAddContent.insert(10, addList.size());
                rolePermAddContent.deleteCharAt(rolePermAddContent.length() - 1);//删除结尾逗号
                moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                        LogConstants.Page.EDIT_ROLE,
                        LogConstants.PageUrl.EDIT_ROLE,
                        LogConstants.OperateType.ADD,
                        LogConstants.ObjectType.ROLE_PERMISSION,
                        role.getId() + "",
                        rolePermAddContent.toString(),
                        request
                );
                //拼接user表更新数据日志内容
                if (StringUtils.isNotBlank(roleUpdateContent)) {
                    roleUpdateContent.append(";");
                }
                roleUpdateContent.append(rolePermAddContent);
            }
        }
        //2、更新role
        //（1）
        Integer updateRoleSuccess = roleMapper.updateRole(role);
        //（2）添加到日志信息
        if (updateRoleSuccess > 0) {
            //获取content
            CompareBeanUtils roleCb = new CompareBeanUtils(Role.class, oldRole, role);
            roleCb.compare("id", "id");
            roleCb.compare("name", "姓名");
            roleCb.compare("status", "状态");
            if (StringUtils.isNotBlank(roleUpdateContent)) {
                roleUpdateContent.append(";");
            }
            if (StringUtils.isNotBlank(roleCb.toResult().toString())) {
                roleUpdateContent.append("[角色表]").append(roleCb.toResult());
            }
            //保存
            moniLogService.saveMonitor(LogConstants.LogType.OPERATE,
                    LogConstants.Page.EDIT_ROLE,
                    LogConstants.PageUrl.EDIT_ROLE,
                    LogConstants.OperateType.UPDATE,
                    LogConstants.ObjectType.ROLE,
                    role.getId() + "",
                    roleUpdateContent.toString(),
                    request
            );
        }
        //3、组织返回信息
        resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
        resultHandler.setMessage("修改角色信息成功");
        return resultHandler;
    }



}
