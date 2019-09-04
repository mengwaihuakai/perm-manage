package com.solid.subscribe.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fanyongju
 * @Title: TaskSteps
 * @date 2019/8/27 14:47
 */
@Data
@Accessors(chain = true)
public class TaskSteps {
    private Integer offer_id;
    private Integer step;
    private String type;
    private String url;
    private String next_on;
    private String js;
}
