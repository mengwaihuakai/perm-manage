package com.solid.subscribe.dao;

import com.solid.subscribe.entity.OfferUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OfferUserDao {
    @Select("select `offer_id`, `user_id` from t_user_info " +
            "where offer_id=#{offerId} and user_id=#{userId}")
    OfferUser selectUserInfo(@Param("offerId") Integer offerId, @Param("userId") String userId);

    @Insert("INSERT INTO `t_user_info` (`offer_id`, `user_id`) " +
            "VALUE (#{offer_id}, #{user_id})")
    Integer insertUserInfo(OfferUser userInfo);
}
