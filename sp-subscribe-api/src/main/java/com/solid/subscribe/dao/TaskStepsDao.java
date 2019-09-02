package com.solid.subscribe.dao;

import com.solid.subscribe.entity.TaskSteps;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskStepsDao {
    @Select("select `offer_id`, `step`, `type`, `url`, `next_on`, `js` from t_task_steps " +
            "where offer_id=#{offerId} " +
            "order by step limit 1")
    TaskSteps selectFirstStepRecordByOfferId(Integer offerId);

    @Select("select `offer_id`, `step`, `type`, `url`, `next_on`, `js` from t_task_steps " +
            "where offer_id=#{offerId} and step > #{step} " +
            "order by step limit 1")
    TaskSteps selecNextStep(@Param("offerId") Integer offerId, @Param("step") Integer step);
}
