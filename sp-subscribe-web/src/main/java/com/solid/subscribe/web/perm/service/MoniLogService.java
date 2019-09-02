package com.solid.subscribe.web.perm.service;

import com.solid.subscribe.web.perm.dao.MoniLogMapper;
import com.solid.subscribe.web.perm.entity.MonitorLog;
import com.solid.subscribe.web.perm.util.InternetProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Service
public class MoniLogService {

    @Autowired(required = false)
    private MoniLogMapper moniLogMapper;


    public void saveMonitor(String logType, String page,String pageUrl, String operateType, String objectType, String objectId,
                            String content, HttpServletRequest request) {
        MonitorLog monitorLog=new MonitorLog();
        monitorLog.setLogType(logType);
        monitorLog.setPage(page);
        monitorLog.setPageUrl(pageUrl);
        monitorLog.setOperateType(operateType);
        monitorLog.setObjectType(objectType);
        monitorLog.setObjectId(objectId+"");
        monitorLog.setContent(content);
        if(null!=request.getSession().getAttribute("userName")){
            monitorLog.setOperatorName(request.getSession().getAttribute("userName").toString());
        }else{
            monitorLog.setOperatorName("");
        }
        if(null!=request.getSession().getAttribute("userAccount")){
            monitorLog.setOperatorAccount(request.getSession().getAttribute("userAccount").toString());
        }else{
            monitorLog.setOperatorAccount("");
        }
        monitorLog.setSystemIp(InternetProtocol.getRemoteAddr(request));
        monitorLog.setUserAgent(request.getHeader("user-agent"));
        monitorLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        moniLogMapper.addLog(monitorLog);
    }

}
