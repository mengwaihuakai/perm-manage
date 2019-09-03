package com.solid.subscribe.web.entity;

import jxl.write.DateTime;
import lombok.Data;

@Data
public class Offer {

    private Integer offer_id;
    private String offer_name;
    private Integer root_offer_id;
    private String type;
    private String pf_description;
    private String platform;
    private String pf_category;
    private String pf_conversion_flow;
    private String pf_conversion_type;
    private Double pf_payout;
    private String pf_kpi;
    private Integer pf_required_deviceid;
    private Integer config_status;
    private Integer status;
    private DateTime effective_date;
    private String url;
    private Integer is_close_wifi;
    private Integer is_notification_enabled;
    private Integer budget;
    private Integer daily_budget;
    private Integer target_country_mode;
    private String target_country;
    private Integer target_operator_mode;
    private String target_operator;
    private Integer target_os_mode;
    private String target_os;

}
