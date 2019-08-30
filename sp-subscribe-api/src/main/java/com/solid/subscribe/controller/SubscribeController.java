package com.solid.subscribe.controller;

import com.alibaba.fastjson.JSON;
import com.solid.subscribe.service.SubscribeService;
import com.solid.subscribe.util.CipherHelper;
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
@RequestMapping("/api")
public class SubscribeController {
    @Autowired
    private SubscribeService subscribeService;
    private CipherHelper cipherHelper = CipherHelper.V3();

    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @PostMapping("/offer")
    public byte[] offer(@RequestBody byte[] body, HttpServletRequest request) {
        OfferRspVo offerRspVo = subscribeService.getOffers(BodyCover(body, "get offer"), request);
        Response response = (offerRspVo == null || CollectionUtils.isEmpty(offerRspVo.getTasks())) ? Response.error("no suitable offer") : Response.getInstance().success(offerRspVo);
        return ResponseCover(response, "get offer");
    }

    @PostMapping("/task/next")
    public byte[] nextTask(@RequestBody byte[] body) {
        OfferRspVo.Task task = subscribeService.getNextStep(BodyCover(body, "next task"));
        Response response = task == null ? Response.error("not has next task") : Response.getInstance().success(new HashMap<String, OfferRspVo.Task>() {{
            put("task", task);
        }});
        return ResponseCover(response, "next task");
    }

    @PostMapping("/task/error")
    public byte[] taskError(@RequestBody byte[] body) {
        subscribeService.taskError(BodyCover(body, "taskError"));
        return ResponseCover(Response.success(), "taskError");
    }

    private String BodyCover(byte[] oriBody, String type) {
        String body = null;
        try {
            body = cipherHelper.decrypt_unzip(oriBody);
        } catch (Exception e) {
            logger.error("{} decrypt_unzip body error: {}", type, e);
        }
        logger.info("{} request:{}", type, body);
        return body;
    }

    private byte[] ResponseCover(Response response, String type) {
        String result = JSON.toJSONString(response);
        byte[] bytes = null;
        try {
            logger.info("{} response:{}", type, result);
            bytes = cipherHelper.encrypt(result);
        } catch (Exception e) {
            logger.error("{} packaging response error:{}", type, e);
        }
        return bytes;
    }
}
