package com.solid.subscribe.entity;

import lombok.Data;

/**
 * @author fanyongju
 * @Title: Offer
 * @date 2019/8/27 11:21
 */
@Data
public class Offer {
    private Integer offer_id;
    private String url;
    private Integer is_notification_enabled;
    private Integer is_close_wifi;
    private Integer budget;
    private Integer daily_budget;
    private Integer target_country_mode;
    private String target_country;
    private Integer target_os_mode;
    private String target_os;
}
