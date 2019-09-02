package com.solid.subscribe.web.perm.entity;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/14.
 */
@Table(name="monitor_log")
public class MonitorLog implements Serializable {
    @Id
    private Integer id;
    //日志类型
    @Column(name="log_type")
    private String logType;
    //页面
    @Column(name = "page")
    private String page;
    //页面资源
    @Column(name = "page_url")
    private String pageUrl;
    //操作类型
    @Column(name = "operate_type")
    private String operateType;
    //对象类型
    @Column(name = "object_type")
    private String objectType;
    //对象id
    @Column(name = "object_id")
    private String objectId;
    //内容
    @Column(name = "content")
    private String content;
    //操作员姓名
    @Column(name = "operator_name")
    private String operatorName;
    //操作员账号
    @Column(name = "operator_account")
    private String operatorAccount;
    //ip
    @Column(name = "system_ip")
    private String systemIp;
    //UA
    @Column(name = "user_agent")
    private String userAgent;
    //创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String oparateType) {
        this.operateType = oparateType;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorAccount() {
        return operatorAccount;
    }

    public void setOperatorAccount(String operatorAccount) {
        this.operatorAccount = operatorAccount;
    }

    public String getSystemIp() {
        return systemIp;
    }

    public void setSystemIp(String systemIp) {
        this.systemIp = systemIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "MonitorLog{" +
                "id=" + id +
                ", logType='" + logType + '\'' +
                ", page='" + page + '\'' +
                ", pageUrl='" + pageUrl + '\'' +
                ", oparateType='" + operateType + '\'' +
                ", objectType='" + objectType + '\'' +
                ", objectId='" + objectId + '\'' +
                ", content='" + content + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", operatorAccount='" + operatorAccount + '\'' +
                ", systemIp=" + systemIp +
                ", userAgent=" + userAgent +
                ", createTime=" + createTime +
                '}';
    }
}
