package com.solid.subscribe.web.perm.service;

import com.solid.subscribe.web.perm.dao.MoniLogMapper;
import com.solid.subscribe.web.perm.entity.MonitorLog;
import com.solid.subscribe.web.perm.util.InternetProtocol;
import com.solid.subscribe.web.perm.util.PageView;
import com.solid.subscribe.web.perm.vo.LogPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

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


    public PageView findLogList(LogPageInfo logPageInfo, PageView pageView, HttpServletRequest request) {
        Integer pageNumber;
        Integer size;
        int totalSize;
        //分页查询
        if (null==logPageInfo.getPageNumber()||logPageInfo.getPageNumber() <= 1){
            logPageInfo.setPageNumber(1);
        }
        if (null==logPageInfo.getPageSize()||logPageInfo.getPageSize() <= 1){
            logPageInfo.setPageSize(10);
        }
        //总记录数
        totalSize=moniLogMapper.findLogCount(logPageInfo);
        //从最后一条开始取记录
        if(totalSize/logPageInfo.getPageSize()>=logPageInfo.getPageNumber()){
            pageNumber=totalSize-logPageInfo.getPageSize()*logPageInfo.getPageNumber();
            size=logPageInfo.getPageSize();
        }else{
            pageNumber=0;
            size=totalSize%logPageInfo.getPageSize();
        }
        logPageInfo.setPageNumber(pageNumber);
        logPageInfo.setPageSize(size);
        List<MonitorLog> monitorLogList=moniLogMapper.findLogList(logPageInfo);
        //按日期倒序排序
        Collections.sort(monitorLogList,(MonitorLog log1, MonitorLog log2)->log2.getCreateTime().compareTo(log1.getCreateTime()));
        pageView.setResult(monitorLogList);
        pageView.setTotalSize(totalSize);
        return pageView;
    }
}
