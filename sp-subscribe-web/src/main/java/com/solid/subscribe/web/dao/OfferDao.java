package com.solid.subscribe.web.dao;

import com.solid.subscribe.web.entity.Offer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OfferDao {

    @Select("select `offer_id`, `offer_name`, `status`, `config_status` from t_offer")
    List<Offer> selectOfferList();

    @Update("update t_offer set status = #{status} where offer_id = #{offer_id}")
    Integer updateOfferStatus(@Param(value = "offer_id") Integer id,
                              @Param(value = "status") Integer status);

}
