package com.solid.subscribe.web.perm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.solid.subscribe.web.perm.entity.Role;
import com.solid.subscribe.web.perm.entity.User;
import com.solid.subscribe.web.perm.service.MoniLogService;
import com.solid.subscribe.web.perm.service.RoleService;
import com.solid.subscribe.web.perm.service.UserService;
import com.solid.subscribe.web.perm.util.Constants;
import com.solid.subscribe.web.perm.util.LogConstants;
import com.solid.subscribe.web.perm.util.PageView;
import com.solid.subscribe.web.perm.util.ResultHandler;
import com.solid.subscribe.web.perm.util.shiro.ShiroConstants;
import com.solid.subscribe.web.perm.vo.UserPageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZOOMY on 2018/8/23.
 */
@Controller
@RequestMapping(value = "perm/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    MoniLogService moniLogService;

    @RequestMapping(value="toUpdatePwd")
    public String toUpdatePwd(HttpServletRequest request){
        try{
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.UPDATE_PWD,
                    LogConstants.PageUrl.UPDATE_PWD,
                    LogConstants.OperateType.VISIT,
                    "",
                    "",
                    "",
                    request);
        }catch(Exception e){
            logger.error("前往更新密码页面失败", e);
            e.printStackTrace();
        }
        return "common/updatePwd";
    }


    @RequestMapping(value="updatePwd",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler updatePwd(String password, String confirmPwd, HttpServletRequest request){
        ResultHandler resultHandler = new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        try{
            if(StringUtils.isNotBlank(password) && StringUtils.isNotBlank(confirmPwd)){
                Subject subject = SecurityUtils.getSubject();
                User user = (User)subject.getPrincipal();
                resultHandler = userService.updatePwd(request, user.getAccount(), password, confirmPwd, resultHandler);
            }else{
                resultHandler.setMessage("Account or password cannot be null");
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("更新密码失败", e);
            resultHandler.setMessage("Failed to modify password, contact administrator");
        }
        return resultHandler;
    }

    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="permUser")
    public String permUser(HttpServletRequest request){
        try{
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.PERM_USER,
                    LogConstants.PageUrl.PERM_USER,
                    LogConstants.OperateType.VISIT,
                    LogConstants.ObjectType.USER,
                    "",
                    "",
                    request);
        }catch (Exception e){
            logger.error("前往用户列表页面失败", e);
            e.printStackTrace();
        }
        return "perm/user/permUser";
    }


    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="search")
    @ResponseBody
    public PageView search(){
        PageView pageView=new PageView();
        try{
            List<UserPageInfo> userPageInfos = userService.getUserPageInfo();
            pageView.setResult(userPageInfos);
        }catch(Exception e){
            logger.error("查询用户信息失败", e);
            e.printStackTrace();
        }
        return pageView;
    }


    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="toAddUser")
    public String toAddUser(Model model, HttpServletRequest request){
        try{
            //获取所有有效的角色
            List<Role> roleList=roleService.getValidRoles();
            model.addAttribute("roleList", JSON.toJSONString(roleList, SerializerFeature.UseSingleQuotes));
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.CREATE_USER,
                    LogConstants.PageUrl.TO_CREATE_USER,
                    LogConstants.OperateType.VISIT,
                    "",
                    "",
                    "",
                    request);
        }catch (Exception e){
            logger.error("前往创建用户页面失败", e);
            e.printStackTrace();
        }
        return "perm/user/addUser";
    }


    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="addUser",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler addUser(@Validated User user, BindingResult bingdingResult, String confirmPwd, String[] roleIdArr, HttpServletRequest request){
        ResultHandler resultHandler =new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        try{
            if(StringUtils.isNotBlank(confirmPwd)&&(null!=roleIdArr&&roleIdArr.length!=0)){
                if(bingdingResult.hasErrors()) {//校验不通过
                    StringBuilder messages=new StringBuilder();
                    for (FieldError fieldError : bingdingResult.getFieldErrors()) { // 获取与给定字段关联的错误
                        messages.append(fieldError.getDefaultMessage());
                        messages.append("\n");
                    }
                    resultHandler.setMessage(messages.toString());
                }else{//校验通过
                    resultHandler =userService.saveUser(request,user,confirmPwd,roleIdArr,resultHandler);
                }
            }else if(StringUtils.isBlank(confirmPwd)){
                resultHandler.setMessage("请确认密码!");
            }else if(null==roleIdArr||roleIdArr.length==0){
                resultHandler.setMessage("请选择角色！");
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("保存用户信息失败", e);
            resultHandler.setMessage("创建用户失败");
        }
        return resultHandler;
    }

    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="toEditUser",method = RequestMethod.POST)
    public String toEditUser(String id,Model model,HttpServletRequest request){
        try{
            //获取所有有效的role
            List<Role> roleList=roleService.getValidRoles();
            model.addAttribute("roleList", JSON.toJSONString(roleList, SerializerFeature.UseSingleQuotes));
            //获取该用户的信息
            UserPageInfo userPageInfo = userService.getUserById(Integer.valueOf(id));
            model.addAttribute("userInfo", JSON.toJSONString(userPageInfo, SerializerFeature.UseSingleQuotes));
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.EDIT_USER,
                    LogConstants.PageUrl.TO_EDIT_USER,
                    LogConstants.OperateType.VISIT,
                    "",
                    "",
                    "",
                    request);
        }catch (Exception e){
            logger.error("前往编辑页面失败", e);
            e.printStackTrace();
        }
        return "perm/user/editUser";
    }

    //前后端分离时 vue-cli :edit/add user页面获取带入的数据
    @RequiresPermissions(ShiroConstants.PERM_USER)
    @GetMapping(value = "getRoleListAndUserInfo")
    @ResponseBody
    public ResultHandler getRoleListAndUserInfo(String id) {
        ResultHandler resultHandler = new ResultHandler();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            //获取所有有效的role
            resultMap.put("roleList", roleService.getValidRoles());
            //获取该用户的信息
            if (StringUtils.isNotBlank(id)) {
                resultMap.put("userInfo", userService.getUserById(Integer.valueOf(id)));
            }
            resultHandler.setCode(0);
            resultHandler.setResultMap(resultMap);
        } catch (Exception e) {
            resultHandler.setCode(1);
            resultHandler.setMessage("获取数据失败");
            logger.error("获取数据失败", e);
            e.printStackTrace();
        }
        return resultHandler;
    }



    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="editUser",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler editUser(@Validated User user, BindingResult bingdingResult, String confirmPwd, String[] roleIdArr, HttpServletRequest request){
        ResultHandler resultHandler =new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        try{
            if(StringUtils.isNotBlank(confirmPwd)&&(null!=roleIdArr&&roleIdArr.length!=0)){
                if(bingdingResult.hasErrors()) {//校验不通过
                    StringBuilder messages=new StringBuilder();
                    for (FieldError fieldError : bingdingResult.getFieldErrors()) { // 获取与给定字段关联的错误
                        messages.append(fieldError.getDefaultMessage());
                        messages.append("\n");
                    }
                    resultHandler.setMessage(messages.toString());
                }else{//校验通过
                    resultHandler =userService.updateUser(request,user,confirmPwd,roleIdArr,resultHandler);
                }
            }else if(StringUtils.isBlank(confirmPwd)){
                resultHandler.setMessage("请确认密码!");
            }else if(null==roleIdArr||roleIdArr.length==0){
                resultHandler.setMessage("请选择角色！");
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("更改用户信息失败", e);
            resultHandler.setMessage("更改用户信息失败");
        }
        return resultHandler;
    }


    @RequiresPermissions(ShiroConstants.PERM_USER)
    @PostMapping(value="deleteUser")
    @ResponseBody
    public ResultHandler deleteUser(@RequestBody User user, HttpServletRequest request){
        ResultHandler resultHandler=new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        Integer id = user.getId();
        try {
            if(null!=id){
                userService.deleteUser(id,request);
                resultHandler.setCode(Constants.Result.RESULTCODE_SUCCESS);
                resultHandler.setMessage("用户删除成功!");
            }else{
                resultHandler.setMessage("请选择用户！");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("删除用户信息失败", e);
            resultHandler.setMessage("用户删除失败!");
        }
        return resultHandler;
    }


    @RequiresPermissions(ShiroConstants.PERM_USER)
    @RequestMapping(value="log",method = RequestMethod.POST)
    public String log(String account,String id,Model model,HttpServletRequest request){
        try{
            model.addAttribute("account",account);
            model.addAttribute("id",id);
            //保存访问日志
            moniLogService.saveMonitor(LogConstants.LogType.VISIT,
                    LogConstants.Page.USER_LOG,
                    LogConstants.PageUrl.USER_LOG,
                    LogConstants.OperateType.VISIT,
                    LogConstants.ObjectType.MONITOR_LOG,
                    "",
                    "",
                    request);
        }catch (Exception e){
            logger.error("前往用户日志页面失败", e);
            e.printStackTrace();
        }
        return "perm/user/userLog";
    }
}
