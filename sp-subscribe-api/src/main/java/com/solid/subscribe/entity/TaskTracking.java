package com.solid.subscribe.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

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
    private Date create_time;
    private String country;
    private String bundle;
    private String user_id;
    private String device_info;
    private String tracks;
}
