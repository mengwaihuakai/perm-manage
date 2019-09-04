package com.solid.subscribe.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fanyongju
 * @Title: UserInfo
 * @date 2019/9/3 18:54
 */
@Data
@Accessors(chain = true,fluent = true)
public class OfferUser {
    private Integer offer_id;
    private String user_id;
}
