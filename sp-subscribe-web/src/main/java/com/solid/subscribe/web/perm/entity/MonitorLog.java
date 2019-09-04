package com.solid.subscribe.web.perm.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ZOOMY on 2018/8/14.
 */
@Table(name="monitor_log")
@Data
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

}
