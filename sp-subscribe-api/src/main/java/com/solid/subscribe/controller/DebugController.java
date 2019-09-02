package com.solid.subscribe.controller;

import com.solid.subscribe.service.SubscribeService;
import com.solid.subscribe.vo.OfferRspVo;
import com.solid.subscribe.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author fanyongju
 * @Title: SubscribeController
 * @date 2019/8/27 14:12
 */
@RestController
@RequestMapping("/debug")
public class DebugController {
    @Autowired
    private SubscribeService subscribeService;

    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @PostMapping("/offer")
    public Response offer(@RequestBody String body, HttpServletRequest request) {
        OfferRspVo offerRspVo = subscribeService.getOffers(body, request);
        return (offerRspVo == null || CollectionUtils.isEmpty(offerRspVo.getTasks())) ? Response.error("no suitable offer") : Response.getInstance().success(offerRspVo);
    }

    @PostMapping("/task/next")
    public Response nextTask(@RequestBody String body, HttpServletRequest request) {
        OfferRspVo.Task task = subscribeService.getNextStep(body, request);
        return task == null ? Response.error("not has next task") : Response.getInstance().success(new HashMap<String, OfferRspVo.Task>() {{
            put("task", task);
        }});
    }

    @PostMapping("/task/error")
    public Response taskError(@RequestBody String body) {
        subscribeService.taskError(body);
        return Response.success();
    }
}