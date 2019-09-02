package com.solid.subscribe.web.perm.util.shiro;

import com.solid.subscribe.web.perm.dao.ShiroSessionMapper;
import com.solid.subscribe.web.perm.entity.ShiroSession;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by ZOOMY on 2019/1/18.
 */
@Component
public class ShiroSessionDao extends EnterpriseCacheSessionDAO {
    @Autowired
    private ShiroSessionMapper shiroSession;

    @Override
    protected Serializable doCreate(Session session) {
        ShiroSession ss = new ShiroSession();
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        ss.setId(sessionId.toString());
        ss.setSession(SerializableUtils.serialize(session));
        ss.setStartTimestamp(session.getStartTimestamp());
        ss.setLastAccessTime(session.getLastAccessTime());
        ss.setTimeout(session.getTimeout());
        ss.setHost(session.getHost());
        shiroSession.createSession(ss);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String session = shiroSession.querySessionById(sessionId.toString());
        if (null == session) {
            return null;
        } else {
            return SerializableUtils.deserialize(session);
        }
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session instanceof ValidatingSession
                && !((ValidatingSession) session).isValid()) {
            return;
        }
        ShiroSession ss = new ShiroSession();
        ss.setId(session.getId().toString());
        ss.setSession(SerializableUtils.serialize(session));
        ss.setStartTimestamp(session.getStartTimestamp());
        ss.setLastAccessTime(session.getLastAccessTime());
        ss.setTimeout(session.getTimeout());
        ss.setHost(session.getHost());
        shiroSession.updateShiroSession(ss);
    }

    @Override
    public void delete(Session session) {
        Serializable sessionId = session.getId();
        shiroSession.deleteSession(sessionId.toString());
    }

}
