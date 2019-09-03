package com.solid.subscribe.web.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fanyongju
 * @Title: SwitchVO
 * @date 2019/1/15 14:43
 */
@Data
public class SwitchVO {
    @NotNull(message = "'id' must not be null")
    private Integer id;
    @NotNull(message = "'status' must not be null")
    private Integer status;
}
