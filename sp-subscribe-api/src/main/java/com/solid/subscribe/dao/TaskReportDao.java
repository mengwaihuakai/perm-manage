package com.solid.subscribe.dao;

import com.solid.subscribe.entity.TaskReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author fanyongju
 * @Title: TaskReportDao
 * @date 2019/9/3 17:42
 */
@Mapper
public interface TaskReportDao {
    @Insert("INSERT INTO `t_task_report` (`task_id`, `offer_id`, `country`, `bundle`, `click`) " +
            "VALUE (#{task_id}, #{offer_id}, #{country}, #{bundle}, #{click}) " +
            "ON DUPLICATE KEY UPDATE click=#{click}")
    Integer updateClick(TaskReport taskReport);

    @Insert("INSERT INTO `t_task_report` (`task_id`, `offer_id`, `country`, `bundle`, `conv`) " +
            "VALUE (#{task_id}, #{offer_id}, #{country}, #{bundle}, #{conv}) " +
            "ON DUPLICATE KEY UPDATE conv=#{conv}")
    Integer updateConv(TaskReport taskReport);

    @Update("update t_task_report set platform_conv=#{pfConv} where task_id=#{taskId}")
    Integer updatePlatformConv(@Param("taskId") String taskId, @Param("pfConv") Integer pfConv);
}
