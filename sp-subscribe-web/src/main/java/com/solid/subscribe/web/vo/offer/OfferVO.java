package com.solid.subscribe.web.vo.offer;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Data
public class OfferVO {
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
    @NotNull(message = "'config_status' must not be null")
    private Integer config_status;
    @NotNull(message = "'config_status' must not be null")
    private Integer status;
    @NotNull(message = "'effective_date' must not be null")
    private Date effective_date;
    @Pattern(regexp = "^http[s]?://.*$", message = "'url' must be a HTTP(S) url")
    private String url;
    @NotNull(message = "'is_close_wifi' must not be null")
    private Integer is_close_wifi;
    @NotNull(message = "'is_notification_enabled' must not be null")
    private Integer is_notification_enabled;
    @NotNull(message = "'budget' must not be null")
    private Integer budget;
    private Integer daily_budget;
    private Integer target_country_mode;
    private List<String> target_country;
    private Integer target_operator_mode;
    private List<String> target_operator;
    private Integer target_os_mode;
    private List<String> target_os;
}
