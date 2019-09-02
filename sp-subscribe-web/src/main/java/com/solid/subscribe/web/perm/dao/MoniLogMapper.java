package com.solid.subscribe.web.perm.dao;

import com.solid.subscribe.web.perm.entity.MonitorLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by ZOOMY on 2018/8/28.
 */
@Mapper
public interface MoniLogMapper {
    /**
     * 添加日志
     */
    Integer addLog(MonitorLog log);

}
