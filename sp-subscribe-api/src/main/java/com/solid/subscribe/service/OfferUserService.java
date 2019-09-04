package com.solid.subscribe.service;

import com.solid.subscribe.dao.OfferUserDao;
import com.solid.subscribe.entity.OfferUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanyongju
 * @Title: UserInfoService
 * @date 2019/9/3 19:21
 */
@Service
public class OfferUserService {
    @Autowired
    private OfferUserDao userInfoDao;


    public Boolean recordSuccessUser(Integer offerId, String userId) {
        return userInfoDao.insertUserInfo(new OfferUser().offer_id(offerId).user_id(userId)) > 0;
    }

    public Boolean hasSuccess(Integer offerId, String userId) {
        return userInfoDao.selectUserInfo(offerId, userId) != null;
    }
}
