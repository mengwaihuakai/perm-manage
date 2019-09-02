package com.solid.subscribe.web.perm.util.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This guy is lazy, nothing left.
 *
 * @author
 */
@Configuration
public class ShiroConfigure {

    private static final Logger logger = LoggerFactory
            .getLogger(ShiroConfigure.class);

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件过滤器
     * </br>1,配置shiro安全管理器接口securityManage;
     * </br>2,shiro 连接约束配置filterChainDefinitions;
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        //shiroFilterFactoryBean对象
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //开始注入拦截器工厂
        // 配置shiro安全管理器 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 自定义过滤器
        HashMap<String, Filter> hashMap = new HashMap<String, Filter>();
        hashMap.put("kickout", myAccessFilter());
        shiroFilterFactoryBean.setFilters(hashMap);

        // 指定要求登录时的链接
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/home");
        // 未授权时跳转的界面;
        /*shiroFilterFactoryBean.setUnauthorizedUrl("/common/error");*/
        // filterChainDefinitions拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置退出过滤器,具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        //本项目权限控制通过注解，此方式留作笔记，实际不起任何作用
        filterChainDefinitionMap.put("/add", "perms[addOperation]");

        // 配置不会被拦截的链接 从上向下顺序判断
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/plugins/**", "anon");
        /*login不进行校验*/
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/tologin", "anon");
        /*filterChainDefinitionMap.put("/perm/user/toForgetPwd", "anon");*/
        filterChainDefinitionMap.put("/perm/user/updatePwd", "anon");
        filterChainDefinitionMap.put("/adx/dataDiff/mailDetail", "anon");
        filterChainDefinitionMap.put("/adx/dataDiff/detail", "anon");

        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put("/**", "kickout,authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        // 指定要求登录时的链接
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");

        logger.debug("Shiro拦截器工厂类注入成功");
        return shiroFilterFactoryBean;
    }


    /*
     * 自定义过滤器
     **/
    @Bean
    public MyAccessFilter myAccessFilter() {
        MyAccessFilter myAccessFilter = new MyAccessFilter();
        myAccessFilter.setOutUrl("/login");
        return myAccessFilter;
    }

    /**
     * shiro安全管理器设置realm认证
     *
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(shiroRealm());
        // 自定义session管理
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }


    /**
     * shiro session的管理
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler();
        scheduler.setInterval(7 * 24 * 60 * 60 * 1000);
        scheduler.setSessionManager(sessionManager);
        sessionManager.setSessionValidationScheduler(scheduler);
        sessionManager.setSessionDAO(getSessionDao());
        // 去掉shiro登录时url里的JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        //全局会话超时时间（单位毫秒），默认30分钟  此处设为7天
        sessionManager.setGlobalSessionTimeout(7 * 24 * 60 * 60 * 1000);
        //是否开启删除无效的session对象  默认为true
        sessionManager.setDeleteInvalidSessions(true);
        //是否开启定时调度器进行检测过期session 默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        sessionManager.setSessionValidationInterval(1 * 60 * 60 * 1000);
        return sessionManager;
    }

    @Bean
    public SessionDAO getSessionDao() {
        ShiroSessionDao sessionDao = new ShiroSessionDao();
        sessionDao.setSessionIdGenerator(sessionIdGenerator());
        return sessionDao;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }


    /**
     * 身份认证realm; (账号密码校验；权限等)
     *
     * @return
     */
    @Bean
    public MyShiroRealm shiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        //此处设置是为了避免大量"No cache or cacheManager properties ..."日志产生
        myShiroRealm.setAuthorizationCachingEnabled(false);
        // 使用自定义的CredentialsMatcher进行密码校验和输错次数限制
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }


    /**
     * 凭证匹配器 （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * 所以我们需要修改下doGetAuthenticationInfo中的代码,更改密码生成规则和校验的逻辑一致即可; ）
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5("")); //
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);//默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        return hashedCredentialsMatcher;//盐值为账户名，在myShiroRealm中用到
    }

    /**
     * @描述：开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * </br>Enable Shiro Annotations for Spring-configured beans. Only run after the lifecycleBeanProcessor(保证实现了Shiro内部lifecycle函数的bean执行) has run
     * </br>不使用注解的话，可以注释掉这两个配置
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
