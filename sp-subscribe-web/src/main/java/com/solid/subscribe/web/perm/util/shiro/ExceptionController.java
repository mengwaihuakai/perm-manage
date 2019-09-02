
package com.solid.subscribe.web.perm.util.shiro;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by zhengdan on 2018/9/2.
 * @ControllerAdvice + @ExceptionHandler 全局处理 Controller 层异常
 * 优点：将 Controller 层的异常和数据校验的异常进行统一处理，减少模板代码，减少编码量，提升扩展性和可维护性。
 缺点：只能处理 Controller 层未捕获（往外抛）的异常，对于 Interceptor（拦截器）层的异常，Spring 框架层的异常，就无能为力了。
 */

@ControllerAdvice
public class ExceptionController {
    /**
     * 无授权异常处理
     * @return  授权异常
     */
    @ExceptionHandler(value = UnauthorizedException.class)//处理访问方法时权限不足问题
    public String UnauthorizedException(HttpServletRequest req, Exception e)  {
        return "common/error";
    }


    /**
     * 无认证异常处理
     * @return  认证异常
     */
    @ExceptionHandler(value = UnauthenticatedException.class)//处理访问认证失败问题
    public String UnauthenticatedException(HttpServletRequest request, HttpServletResponse response, Exception z)  {
        try{
            //重定向到登陆页面
            WebUtils.issueRedirect(request, response, "/login");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
