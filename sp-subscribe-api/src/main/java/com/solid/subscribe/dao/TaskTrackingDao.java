package com.solid.subscribe.dao;

import com.solid.subscribe.entity.TaskTracking;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskTrackingDao {
    @Select("select count(*) from t_task_tracking " +
            "where offer_id=#{offerId} and state=0 and step=1 and TIMESTAMPDIFF(MINUTE,create_time, NOW()) <= 30")
    Integer selectUnConfirmCount(Integer offerId);

    @Select("select tracks from t_task_tracking " +
            "where task_id=#{taskId} and step=#{step}")
    String selectHtml(@Param("taskId") String taskId,@Param("step") Integer step);

    @Select("select count(*) from t_task_tracking where offer_id=#{offerId} and state=1 and step=(select MAX(step) from t_task_steps where offer_id=#{offerId}) " +
            "and TO_DAYS(create_time)=TO_DAYS(NOW())")
    Integer selectDailyConfirmCount(Integer offerId);

    @Select("select count(*) from t_task_tracking where offer_id=#{offerId} and state=1 and step=(select MAX(step) from t_task_steps where offer_id=#{offerId})")
    Integer selectAllConfirmCount(Integer offerId);

    @Select("select count(*) from t_task_tracking where offer_id=#{offerId} and state=1 and step=(select MAX(step) from t_task_steps where offer_id=#{offerId}) and user_id=#{userId}")
    Integer selectSuccessByUserId(@Param("offerId") Integer offerId, @Param("userId") String userId);

    @Insert("INSERT INTO `t_task_tracking` (`task_id`, `step`, `offer_id`, `state`, country, bundle, user_id, `device_info`, `tracks`) " +
            "VALUE (#{task_id}, #{step}, #{offer_id}, #{state}, #{country}, #{bundle}, #{user_id}, #{device_info}, #{tracks})" +
            "ON DUPLICATE KEY UPDATE state=#{state}, device_info=#{device_info}, tracks=#{tracks}")
    Integer insertTaskTracking(TaskTracking taskTracking);
}
