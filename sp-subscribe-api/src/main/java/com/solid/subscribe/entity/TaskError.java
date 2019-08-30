package com.solid.subscribe.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fanyongju
 * @Title: TaskError
 * @date 2019/8/27 14:56
 */
@Data
@Accessors(chain = true)
public class TaskError {
    private String task_id;
    private Integer step;
    private Integer offer_id;
    private String error_type;
    private String error_msg;
    private String device_info;
    private String tracks;
}
