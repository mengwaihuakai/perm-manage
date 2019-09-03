package com.solid.subscribe.web.dao;

import com.solid.subscribe.web.entity.Offer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OfferDao {

    @Select("select `offer_id`, `offer_name`, `status`, `config_status` from t_offer")
    List<Offer> selectOfferList();

    @Update("update t_offer set status = #{status} where offer_id = #{offer_id}")
    Integer updateOfferStatus(@Param(value = "offer_id") Integer id,
                              @Param(value = "status") Integer status);

    @Select("select `offer_id`, `offer_name`, `root_offer_id`, `type`, `platform`, `pf_description`, `pf_category`, " +
            "`pf_conversion_flow`, `pf_conversion_type`, `pf_payout`, `pf_kpi`, `pf_required_deviceid`, `config_status`, " +
            "`status`, `effective_date`, `url`, `is_close_wifi`, `is_notification_enabled`, `budget`, `daily_budget`, " +
            "`target_country_mode`, `target_country`, `target_operator_mode`, `target_operator`, `target_os_mode`, `target_os` " +
            "from t_offer " +
            "where offer_id=#{offer_id}")
    Offer selectOffer(@Param(value = "offer_id") Integer id);

    @Insert("INSERT INTO `t_offer` (`offer_name`, `root_offer_id`, `type`, `platform`, `pf_description`, `pf_category`, " +
            "`pf_conversion_flow`, `pf_conversion_type`, `pf_payout`, `pf_kpi`, `pf_required_deviceid`, `config_status`, " +
            "`status`, `effective_date`, `url`, `is_close_wifi`, `is_notification_enabled`, `budget`, `daily_budget`, " +
            "`target_country_mode`, `target_country`, `target_operator_mode`, `target_operator`, `target_os_mode`, `target_os` )" +
            "VALUES (#{offer_name}, #{root_offer_id}, #{type}, #{platform}, #{pf_description}, #{pf_category}, " +
                            "#{pf_conversion_flow}, #{pf_conversion_type}, #{pf_payout}, #{pf_kpi}, #{pf_required_deviceid}, #{config_status}," +
                            "#{status}, #{effective_date}, #{url}, #{is_close_wifi}, #{is_notification_enabled}, #{budget}, #{daily_budget}," +
                            "#{target_country_mode}, #{target_country}, #{target_operator_mode}, #{target_operator}, #{target_os_mode}, #{target_os} )")
    Integer insertOffer(Offer offer);

    @Update("UPDATE `t_offer` SET `offer_name`=#{offer_name}, `root_offer_id`=#{root_offer_id}, `type`=#{type}, `platform`=#{platform}, `pf_description`=#{pf_description}," +
            " `pf_category`=#{pf_category}, `pf_conversion_flow`=#{pf_conversion_flow}, `pf_conversion_type`=#{pf_conversion_type}, `pf_payout`=#{pf_payout}, `pf_kpi`=#{pf_kpi}, " +
            "`pf_required_deviceid`=#{pf_required_deviceid}, `config_status`=#{config_status}, `status`=#{status}, `effective_date`=#{effective_date}, `url`=#{url}, " +
            "`is_close_wifi`=#{is_close_wifi}, `is_notification_enabled`=#{is_notification_enabled}, `budget`=#{budget}, `daily_budget`=#{daily_budget}, " +
            "`target_country_mode`=#{target_country_mode}, `target_country`=#{target_country}, `target_operator_mode`=#{target_operator_mode}, `target_operator`=#{target_operator}, " +
            "`target_os_mode`=#{target_os_mode}, `target_os`=#{target_os}" +
            "WHERE (`offer_id`=#{offer_id})")
    Integer updateOffer(Offer offer);
}
