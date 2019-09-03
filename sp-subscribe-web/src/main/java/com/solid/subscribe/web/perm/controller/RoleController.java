package com.solid.subscribe.web.perm.controller;

import com.solid.subscribe.web.perm.entity.Permission;
import com.solid.subscribe.web.perm.entity.Role;
import com.solid.subscribe.web.perm.service.MoniLogService;
import com.solid.subscribe.web.perm.service.PermissionService;
import com.solid.subscribe.web.perm.service.RoleService;
import com.solid.subscribe.web.perm.util.Constants;
import com.solid.subscribe.web.perm.util.LogConstants;
import com.solid.subscribe.web.perm.util.PageView;
import com.solid.subscribe.web.perm.util.ResultHandler;
import com.solid.subscribe.web.perm.util.shiro.ShiroConstants;
import com.solid.subscribe.web.perm.vo.RolePageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "perm/role")
public class RoleController {
    @Autowired
    MoniLogService moniLogService;
    @Autowired
    RoleService roleService;
    @Autowired(required = false)
    PermissionService permissionService;

    @RequiresPermissions(ShiroConstants.PERM_ROLE)
    @RequestMapping(value="permRole")
    public String permUser(HttpServletRequest request){
        try{
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.PERM_ROLE,
                    LogConstants.PageUrl.PERM_ROLE,
                    LogConstants.OperateType.VISIT,
                    LogConstants.ObjectType.ROLE,
                    "",
                    "",
                    request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "perm/role/permRole";
    }


    @RequiresPermissions(ShiroConstants.PERM_ROLE)
    @RequestMapping(value="search")
    @ResponseBody
    public PageView search(Role roleParam, HttpServletRequest request){
        PageView pageView=new PageView();
        try{
            List<RolePageInfo> rolePageInfos=roleService.getRolePageInfo(roleParam,request);
            pageView.setResult(rolePageInfos);
        }catch(Exception e){
            e.printStackTrace();
        }
        return pageView;
    }

    @RequiresPermissions(ShiroConstants.PERM_ROLE)
    @RequestMapping(value="toAddRole")
    public String toAddRole(Model model, HttpServletRequest request){
        try{
            //分类获取所有有效的权限
            List<Permission> permissionList=permissionService.getValidPermissions();
            model.addAttribute("permissionList",permissionList);
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.CREATE_ROLE,
                    LogConstants.PageUrl.TO_CREATE_ROLE,
                    LogConstants.OperateType.VISIT,
                    "",
                    "",
                    "",
                    request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "perm/role/addRole";
    }

    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="addRole",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler addUser(@Validated Role role, BindingResult bindingResult, String[] permissionIdArr, HttpServletRequest request){
        ResultHandler resultHandler =new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        try{
            if(null!=permissionIdArr&&permissionIdArr.length!=0){
                if(bindingResult.hasErrors()) {//校验不通过
                    StringBuilder messages = new StringBuilder();
                    for (FieldError fieldError : bindingResult.getFieldErrors()) { // 获取与给定字段关联的错误
                        messages.append(fieldError.getDefaultMessage());
                        messages.append("\n");
                    }
                    resultHandler.setMessage(messages.toString());
                }else{//校验通过
                    resultHandler =roleService.saveRole(request,role,permissionIdArr,resultHandler);
                }
            }else if(null==permissionIdArr||permissionIdArr.length==0){
                resultHandler.setMessage("请选择权限！");
            }
        }catch(Exception e){
            e.printStackTrace();
            resultHandler.setMessage("创建角色失败");
        }
        return resultHandler;
    }

    @RequiresPermissions(ShiroConstants.PERM_ROLE)
    @RequestMapping(value="toEditRole",method = RequestMethod.POST)
    public String toEditRole(String id,Model model,HttpServletRequest request){
        try{
            //获取所有有效的rpermission
            List<Permission> permissionList=permissionService.getValidPermissions();
            //获取该角色的信息
            Role roleParam=new Role();
            roleParam.setId(Integer.valueOf(id));
            List<RolePageInfo> rolePageInfos=roleService.getRolePageInfo(roleParam,request);
            model.addAttribute("roleInfo",rolePageInfos.get(0));
            model.addAttribute("permissionList",permissionList);
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.EDIT_ROLE,
                    LogConstants.PageUrl.TO_EDIT_ROLE,
                    LogConstants.OperateType.VISIT,
                    "",
                    "",
                    "",
                    request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "perm/role/editRole";
    }

    @RequiresPermissions(ShiroConstants.PERM_ROLE)
    @RequestMapping(value="editRole",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler editRole(@Validated Role role, BindingResult bingdingResult, String[] permissionIdArr, HttpServletRequest request){
        ResultHandler resultHandler =new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        try{
            if(null!=permissionIdArr&&permissionIdArr.length!=0){
                if(bingdingResult.hasErrors()) {//校验不通过
                    StringBuilder messages=new StringBuilder();
                    for (FieldError fieldError : bingdingResult.getFieldErrors()) { // 获取与给定字段关联的错误
                        messages.append(fieldError.getDefaultMessage());
                        messages.append("\n");
                    }
                    resultHandler.setMessage(messages.toString());
                }else{//校验通过
                    resultHandler =roleService.updateRole(request,role,permissionIdArr,resultHandler);
                }
            }else if(null==permissionIdArr||permissionIdArr.length==0){
                resultHandler.setMessage("请选择权限！");
            }
        }catch(Exception e){
            e.printStackTrace();
            resultHandler.setMessage("更改用户信息失败");
        }
        return resultHandler;
    }


    @RequiresPermissions(ShiroConstants.PERM_ROLE)
    @RequestMapping(value="deleteRole",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler deleteRole(@RequestBody Role role, HttpServletRequest request){
        ResultHandler resultHandler=new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        Integer id = role.getId();
        try {
            if(null!=id){
                roleService.deleteRole(id,request);
                resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
                resultHandler.setMessage("角色删除成功!");
            }else{
                resultHandler.setMessage("请选择角色！");
            }
        }catch (Exception e){
            e.printStackTrace();
            resultHandler.setMessage("角色删除失败!");
        }
        return resultHandler;
    }

    @RequiresPermissions(ShiroConstants.PERM_ROLE)
    @RequestMapping(value="log",method = RequestMethod.POST)
    public String log(String name,String id,Model model,HttpServletRequest request){
        try{
            model.addAttribute("name",name);
            model.addAttribute("id",id);
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.ROLE_LOG,
                    LogConstants.PageUrl.ROLE_LOG,
                    LogConstants.OperateType.VISIT,
                    LogConstants.ObjectType.MONITOR_LOG,
                    "",
                    "",
                    request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "perm/role/roleLog";
    }

}
