package com.solid.subscribe.web.perm.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LogPageInfo {
    private Integer id;
    private String logType;
    private String page;
    private String pageUrl;
    private String operateType;
    private String objectType;
    private String objectId;
    private String content;
    private String operatorName;
    private String operatorAccount;
    private String systemIp;
    private String userAgent;
    private Timestamp createTime;
    //每页记录数
    private Integer pageSize=10;
    //当前页
    private Integer pageNumber=1;
}
