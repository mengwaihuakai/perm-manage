package com.solid.subscribe.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * @author fanyongju
 * @Title: OfferVo
 * @date 2019/8/27 11:27
 */
@Getter
public class OfferVo {
    private Data offer;
    private Map<String, String> targeting;

    @Builder
    public OfferVo(Data offer, Map<String, String> targeting) {
        this.offer = offer;
        this.targeting = targeting;
    }

    @ToString
    @Getter
    public static class Data {
        private Integer offer_id;
        private String url;
        private Integer isNotificationEnabled;
        private Integer isCloseWifi;
        private Integer budget;
        private Integer dailyBudget;

        @Builder
        public Data(Integer offer_id, String url, Integer isNotificationEnabled, Integer isCloseWifi, Integer budget, Integer dailyBudget) {
            this.offer_id = offer_id;
            this.url = url;
            this.isNotificationEnabled = isNotificationEnabled;
            this.isCloseWifi = isCloseWifi;
            this.budget = budget;
            this.dailyBudget = dailyBudget;
        }
    }
}
