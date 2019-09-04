package com.solid.subscribe.web.perm.util.shiro;

import com.solid.subscribe.web.perm.dao.UserMapper;
import com.solid.subscribe.web.perm.entity.User;
import com.solid.subscribe.web.perm.util.Constants;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by ZOOMY on 2018/9/4.
 */
public class MyAccessFilter extends AccessControlFilter {
    @Autowired
    UserMapper userMapper;

    private String outUrl;

    public void setOutUrl(String outUrl) {
        this.outUrl = outUrl;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        try {
            Subject subject = getSubject(request, response);
            if (subject.isAuthenticated() || subject.isRemembered()) {//认证通过或者记住过
                User loginUser = (User) subject.getPrincipal();
                User databaseUser = userMapper.getUserById(loginUser.getId());
                //状态是冻结状态或者无效状态时，清除账号信息退出登录
                if (databaseUser.getStatus() != Constants.Status.VALID) {
                    //退出登录
                    subject.logout();
                    //重定向
                    saveRequest(request);
                    WebUtils.issueRedirect(request, response, outUrl);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
