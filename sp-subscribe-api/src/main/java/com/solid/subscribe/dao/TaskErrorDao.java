package com.solid.subscribe.dao;

import com.solid.subscribe.entity.TaskError;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskErrorDao {
    @Insert("INSERT INTO `t_task_error` (`task_id`, `step`, `offer_id`, `error_type`, `error_msg`, `device_info`, `tracks`) " +
            "VALUE (#{task_id}, #{step}, #{offer_id}, #{error_type}, #{error_msg}, #{device_info}, #{tracks}) " +
            "ON DUPLICATE KEY UPDATE error_type=#{error_type}, error_msg=#{error_msg}, `device_info`=#{device_info}, tracks=#{tracks}")
    Integer insertTaskError(TaskError taskError);
}
