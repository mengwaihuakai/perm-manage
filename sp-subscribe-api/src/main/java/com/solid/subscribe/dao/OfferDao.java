package com.solid.subscribe.dao;

import com.solid.subscribe.entity.Offer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author fanyongju
 * @Title: OfferDao
 * @date 2019/8/27 11:45
 */
@Mapper
public interface OfferDao {
    @Select("select `offer_id`, `url`, is_close_wifi, budget, daily_budget, `target_country_mode`, `target_country` from t_offer")
    List<Offer> selectAllOffer();
}
