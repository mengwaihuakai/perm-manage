package com.solid.subscribe.web.perm.controller;


import com.solid.subscribe.web.perm.entity.Role;
import com.solid.subscribe.web.perm.entity.User;
import com.solid.subscribe.web.perm.service.RoleService;
import com.solid.subscribe.web.perm.service.UserService;
import com.solid.subscribe.web.perm.util.Constants;
import com.solid.subscribe.web.perm.util.ResultHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * Created by ZOOMY on 2018/8/20.
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @RequestMapping(value="login")
    public String login(){
        return"login";
    }

    @RequestMapping(value="tologin",method = RequestMethod.POST)
    @ResponseBody
    public ResultHandler toLogin(@RequestBody User paramUser, HttpServletRequest request, HttpSession session){
        ResultHandler resultHandler=new ResultHandler();
        String account = paramUser.getAccount();
        String password = paramUser.getPassword();
        try {
            resultHandler.setCode(Constants.Result.RESULTCODE_FAILURE);
            if(StringUtils.isNotBlank(account)&&StringUtils.isNotBlank(password)){
                //身份验证
                UsernamePasswordToken token = new UsernamePasswordToken(account, password);
                Subject subject = SecurityUtils.getSubject();
                subject.login(token);
                User user = (User)subject.getPrincipal();
                logger.info("对用户[" + account + "]进行登录验证..验证通过,user为："+user);
                //保存登陆信息
                if(null != user){
                    session.setAttribute("userAccount",account);//将账号存到seesion中
                    userService.saveLoginIpAndTime(request,user,resultHandler);
                }else{
                    resultHandler.setMessage("Did not get user information, please contact the administrator");
                }
            }else{
                resultHandler.setMessage("Account or password cannot be null");
            }
        } catch(UnknownAccountException uae){
            logger.info("对用户[" + account + "]进行登录验证..验证未通过,未知账户");
            resultHandler.setMessage("Account does not exist");
        } catch(IncorrectCredentialsException ice){
            logger.info("对用户[" + account + "]进行登录验证..验证未通过,错误密码");
                resultHandler.setMessage("The password is incorrect");
        } catch(LockedAccountException lae){
            logger.info("对用户[" + account + "]进行登录验证..验证未通过,账户已冻结");
            resultHandler.setMessage("Account is frozen");
        } catch (DisabledAccountException lae) {
            logger.info("对用户[" + account + "]进行登录验证..验证未通过,账户状态无效");
            resultHandler.setMessage("Invalid account status");
        } catch (ExcessiveAttemptsException eae) {
            logger.info("对用户[" + account + "]进行登录验证..验证未通过,错误次数过多");
            resultHandler.setMessage("Too many incorrect username or password");
        } catch (AuthenticationException ae) {
            logger.info("对用户[" + account + "]进行登录验证..验证未通过,堆栈轨迹如下");
            ae.printStackTrace();
            resultHandler.setMessage("Username or password is incorrect");
        }catch(Exception e){
            e.printStackTrace();
            resultHandler.setMessage("Please contact the administrator");
        }
        return resultHandler;
    }


    @RequestMapping(value="/")
    public String redirect(){
        return "redirect:/index";
    }

    @RequestMapping(value="/index")
    public String index(){
        return "index";
    }

    @RequestMapping(value="/error")
    public String error(){
        return "common/error";
    }
}
