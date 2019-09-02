package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.ShiroSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by ZOOMY on 2019/1/17.
 */
@Mapper
public interface ShiroSessionMapper {
    void createSession(ShiroSession shiroSession);
    void deleteSession(String sessionId);
    String querySessionById(String sessionId);
    void updateShiroSession(ShiroSession shiroSession);
}
