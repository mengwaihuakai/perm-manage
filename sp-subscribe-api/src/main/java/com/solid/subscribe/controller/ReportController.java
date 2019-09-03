package com.solid.subscribe.controller;

import com.solid.subscribe.vo.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanyongju
 * @Title: ReportController
 * @date 2019/9/2 19:32
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @PostMapping("/revenue")
    public Response report(){
        return null;
    }
}
