package com.solid.subscribe.web.perm.controller;

import com.solid.subscribe.web.perm.entity.User;
import com.solid.subscribe.web.perm.service.MoniLogService;
import com.solid.subscribe.web.perm.service.RoleService;
import com.solid.subscribe.web.perm.service.UserService;
import com.solid.subscribe.web.perm.util.Constants;
import com.solid.subscribe.web.perm.util.LogConstants;
import com.solid.subscribe.web.perm.util.ResultHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ZOOMY on 2018/8/23.
 */
@Controller
@RequestMapping(value = "perm/user")
public class UserController {
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
            e.printStackTrace();
        }
        return "common/updatePwd";
    }


    @RequestMapping(value="updatePwd",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler updatePwd(String pwd, String confirmPwd, HttpServletRequest request){
        ResultHandler resultHandler =new ResultHandler();
        resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
        try{
            if(StringUtils.isNotBlank(pwd) && StringUtils.isNotBlank(confirmPwd)){
                Subject subject = SecurityUtils.getSubject();
                User user=(User)subject.getPrincipal();
                resultHandler =userService.updatePwd(request,user.getAccount(),pwd,confirmPwd,resultHandler);
            }else{
                resultHandler.setMessage("Account or password cannot be null");
            }
        }catch(Exception e){
            e.printStackTrace();
            resultHandler.setMessage("Failed to modify password, contact administrator");
        }
        return resultHandler;
    }
}
