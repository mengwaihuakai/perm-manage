package com.solid.subscribe.service;

import com.solid.subscribe.dao.TaskReportDao;
import com.solid.subscribe.entity.TaskReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanyongju
 * @Title: TaskReportService
 * @date 2019/9/3 18:37
 */
@Service
public class TaskReportService {
    @Autowired
    private TaskReportDao taskReportDao;

    public Boolean recordClick(TaskReport taskReport) {
        return taskReportDao.updateClick(taskReport.click(1)) > 0;
    }

    public Boolean recordConv(TaskReport taskReport) {
        return taskReportDao.updateConv(taskReport.conv(1)) > 0;
    }

    public Boolean recordPlatformConv(String taskId) {
        return taskReportDao.updatePlatformConv(taskId, 1) > 0;
    }
}
