package com.solid.subscribe.web.perm.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by ZOOMY on 2019/1/17.
 */
@Table(name="perm_shiro_session")
public class ShiroSession implements Serializable {
    @Id
    private String id;
    private String session;
    private Date startTimestamp;
    private Date lastAccessTime;
    private Long timeout;
    private String host;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
