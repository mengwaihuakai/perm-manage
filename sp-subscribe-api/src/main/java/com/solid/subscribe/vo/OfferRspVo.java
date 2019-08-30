package com.solid.subscribe.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author fanyongju
 * @Title: OfferRspVo
 * @date 2019/8/27 14:21
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferRspVo {
    private Integer interval;
    private List<Task> tasks;

    @Data
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Task {
        private Integer offer_id;
        private String task_id;
        private String type;
        private String url;
        private Integer isCloseWifi;
        private String next_on;
        private Integer step;
        private String js;
    }
}
