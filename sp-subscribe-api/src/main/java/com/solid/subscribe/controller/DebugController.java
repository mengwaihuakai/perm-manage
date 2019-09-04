package com.solid.subscribe.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solid.subscribe.dao.TaskTrackingDao;
import com.solid.subscribe.service.SubscribeService;
import com.solid.subscribe.vo.OfferRspVo;
import com.solid.subscribe.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    @Autowired
    private TaskTrackingDao taskTrackingDao;

    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);
    private ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);


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

    @GetMapping("/html")
    public String html(String taskId, Integer step) {
        String data = taskTrackingDao.selectHtml(taskId, step);
        JsonNode node = null;
        try {
            node = objectMapper.readTree(data);
        } catch (IOException e) {
            logger.error("parse string to json fail", e);
        }
        if(node != null && node.has("html_content_list") && node.get("html_content_list").get(0).has("html")){
            return node.get("html_content_list").get(0).get("html").asText();
        }
        return null;
    }
}