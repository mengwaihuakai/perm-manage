package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.MonitorLog;
import com.solid.subscribe.web.perm.vo.LogPageInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by ZOOMY on 2018/8/28.
 */
@Mapper
public interface MoniLogMapper {
    /**
     * 添加日志
     */
    Integer addLog(MonitorLog log);
    /**
     * 查询日志
     */
    public List<MonitorLog> findLogList(LogPageInfo logPageInfo);
    /*
     * 记录总数
     **/
    public Integer findLogCount(LogPageInfo logPageInfo);

}
