package com.solid.subscribe.service;

import com.solid.subscribe.dao.TaskTrackingDao;
import com.solid.subscribe.vo.OfferVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanyongju
 * @Title: TaskTrackingService
 * @date 2019/8/27 16:32
 */
@Service
public class TaskTrackingService {
    @Autowired
    private TaskTrackingDao taskTrackingDao;

    private static final Logger logger = LoggerFactory.getLogger(TaskTrackingService.class);

    public Boolean budgetNotEnough(OfferVo.Data offer) {//true代表预算不足
        Integer offerId = offer.getOffer_id();
        Integer unConfirmCount = taskTrackingDao.selectUnConfirmCount(offerId);
        if (offer.getDailyBudget() - unConfirmCount - taskTrackingDao.selectDailyConfirmCount(offerId) <= 0) {
            logger.error("offer {} daily budget is not enough", offerId);
            return true;
        }
        if (offer.getBudget() - unConfirmCount - taskTrackingDao.selectAllConfirmCount(offerId) <= 0) {
            logger.error("offer {} total budget is not enough", offerId);
            return true;
        }
        return false;
    }
}
