package com.solid.subscribe.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fanyongju
 * @Title: TaskTracking
 * @date 2019/8/27 14:49
 */
@Data
@Accessors(chain = true)
public class TaskTracking {
    private String task_id;
    private Integer step;
    private Integer offer_id;
    private Integer state;
    private String device_info;
    private String tracks;
}
