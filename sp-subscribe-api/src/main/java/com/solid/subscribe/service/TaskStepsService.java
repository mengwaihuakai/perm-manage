package com.solid.subscribe.service;

import com.solid.subscribe.dao.TaskStepsDao;
import com.solid.subscribe.entity.TaskSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanyongju
 * @Title: TaskStepsService
 * @date 2019/9/4 11:47
 */
@Service
public class TaskStepsService {
    @Autowired
    private TaskStepsDao taskStepsDao;

   public Boolean isFirstStep(Integer offerId,Integer step){//判断该步是不是第一步
        TaskSteps taskSteps = taskStepsDao.selectFirstStepRecordByOfferId(offerId);
        return step.equals(taskSteps.getStep());
    }
}
