package com.solid.subscribe.web.perm.util.shiro;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ZOOMY on 2018/9/3.
 * Shiro标签中只有hasAnyRole这个标签，却没有对应的HasAnyPermissions
 * 所以自定义HasAnyPermissions标签
 */
@Component
public class HasAnyPermissions implements TemplateMethodModelEx {
    @Override
    public Object exec(List list) throws TemplateModelException {
        if(list.size()<=0){
            throw new TemplateModelException("HasAnyPermissions - 错误参数");
        }
        Subject subject = SecurityUtils.getSubject();
        int index = 0;
        for (Object s : list){
            index++;
            SimpleScalar str = (SimpleScalar ) s;
            try {
                subject.checkPermission(str.getAsString());
            }catch (Exception e){
                if(index==list.size()){
                    return false;
                }
                continue;
            }
            return true;
        }
        return false;
    }
}
