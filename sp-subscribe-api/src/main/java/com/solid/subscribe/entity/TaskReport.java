package com.solid.subscribe.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author fanyongju
 * @Title: TaskReport
 * @date 2019/9/3 17:31
 */
@Data
@Accessors(chain = true, fluent = true)
public class TaskReport {
    private String task_id;
    private Integer offer_id;
    private Date create_time;
    private String country;
    private String bundle;
    private Integer click;
    private Integer conv;
    private Integer platform_conv;
}
