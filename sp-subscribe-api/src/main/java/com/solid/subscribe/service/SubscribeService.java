package com.solid.subscribe.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solid.subscribe.constant.Constant;
import com.solid.subscribe.dao.TaskErrorDao;
import com.solid.subscribe.dao.TaskStepsDao;
import com.solid.subscribe.dao.TaskTrackingDao;
import com.solid.subscribe.entity.TaskError;
import com.solid.subscribe.entity.TaskSteps;
import com.solid.subscribe.entity.TaskTracking;
import com.solid.subscribe.util.GeoIp;
import com.solid.subscribe.util.IdWorker;
import com.solid.subscribe.vo.OfferRspVo;
import com.solid.subscribe.vo.OfferVo;
import com.solid.subscribe.vo.Tuple2;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author fanyongju
 * @Title: SubscribeService
 * @date 2019/8/27 15:00
 */
@Service
public class SubscribeService {
    @Autowired
    private OfferService offerService;
    @Autowired
    private TaskStepsDao taskStepsDao;
    @Autowired
    private TaskTrackingDao taskTrackingDao;
    @Autowired
    private TaskTrackingService taskTrackingService;
    @Autowired
    private TaskErrorDao taskErrorDao;
    @Autowired
    private GeoIp geoIp;
    private static final Logger logger = LoggerFactory.getLogger(SubscribeService.class);

    private static ExpiringMap<Integer, Set<String>> clientOfferMap = ExpiringMap.builder().expiration(1, TimeUnit.DAYS)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();//失效时间为一天
    private ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);


    public OfferRspVo getOffers(String body, HttpServletRequest req) {
        Tuple2<String, String> tuple2 = getUserIdFromBody(body);
        String userId = tuple2.getT1();
        GeoIp.GeoInfo geoInfo = geoIp.findGeoInfoByRequest(req);
        if (geoInfo == null || StringUtils.isEmpty(geoInfo.getCountry())) {
            logger.error("geoinfo is null");
            return null;
        }
        Map<Integer, OfferVo.Data> offerVoMap = offerService.getOfferIds(new HashMap<String, String>() {{
            put("country", geoInfo.getCountry());
        }});
        List<Integer> offerIds = new ArrayList<>(offerVoMap.keySet());
        List<OfferRspVo.Task> tasks = new ArrayList<>();
        Random random = new Random();
        int size = offerIds.size();
        while (tasks.size() < 1) {
            Integer offerId = offerIds.get(random.nextInt(size));//随机选择一个offerId
            if (taskTrackingService.budgetNotEnough(offerVoMap.get(offerId))) {//预算不足
                continue;
            }
            /*if (clientOfferMap.getOrDefault(offerId, new HashSet<>()).contains(userId)) {//如果今天这个offer已经推送给该userId
                continue;
            }*/
            TaskSteps taskSteps = taskStepsDao.selectMinStepRecordByOfferId(offerId);
            if (taskSteps == null) {//该offerid没有step存在
                continue;
            }
            OfferRspVo.Task task = new OfferRspVo.Task()
                    .setOffer_id(offerId)
                    .setStep(taskSteps.getStep())
                    .setTask_id(IdWorker.generateId().toString())
                    .setUrl(taskSteps.getUrl())
                    .setIsCloseWifi(offerVoMap.get(offerId).getIsCloseWifi())
                    .setType(taskSteps.getType())
                    .setNext_on(taskSteps.getNext_on())
                    .setJs(taskSteps.getJs());
            tasks.add(task);
            taskTrackingDao.insertTaskTracking(new TaskTracking()
                    .setTask_id(task.getTask_id())
                    .setStep(task.getStep())
                    .setOffer_id(offerId)
                    .setDevice_info(tuple2.getT2())
                    .setState(Constant.STATE_PENDING));//记录tracking到db
            clientOfferMap.computeIfAbsent(offerId, v -> new HashSet<>()).add(userId);//记录下该userid与offerid的关联关系
        }
        return new OfferRspVo()
                .setInterval(10)
                .setTasks(tasks);

    }

    public OfferRspVo.Task getNextStep(String body) {
        JsonNode request;
        try {
            request = objectMapper.readTree(body);
        } catch (IOException e) {
            logger.error("get next step parse string to json fail", e);
            return null;
        }
        if (request.has("task")) {
            String tracks = request.has("tracks") ? request.get("tracks").toString() : null;
            String client = request.has("client") ? request.get("client").toString() : null;
            JsonNode taskNode = request.get("task");
            String taskId = taskNode.has("task_id") ? taskNode.get("task_id").asText() : null;
            Integer step = taskNode.has("step") ? taskNode.get("step").asInt() : null;
            Integer offerId = taskNode.has("offer_id") ? taskNode.get("offer_id").asInt() : null;
            if (ObjectUtils.allNotNull(taskId, step, offerId)) {
                taskTrackingDao.insertTaskTracking(new TaskTracking()
                        .setTask_id(taskId)
                        .setStep(step)
                        .setOffer_id(offerId)
                        .setDevice_info(client)
                        .setTracks(tracks)
                        .setState(Constant.STATE_SUCCESS));//更新tracks到db
                TaskSteps taskSteps = taskStepsDao.selecNextStep(offerId, step);
                if (taskSteps == null) {
                    return null;//没有下一步任务，返回null
                }
                OfferRspVo.Task task = new OfferRspVo.Task()
                        .setOffer_id(offerId)
                        .setStep(taskSteps.getStep())
                        .setTask_id(IdWorker.generateId().toString())
                        .setUrl(taskSteps.getUrl())
                        .setType(taskSteps.getType())
                        .setNext_on(taskSteps.getNext_on())
                        .setJs(taskSteps.getJs());
                taskTrackingDao.insertTaskTracking(new TaskTracking()
                        .setTask_id(task.getTask_id())
                        .setStep(task.getStep())
                        .setOffer_id(offerId)
                        .setDevice_info(client)
                        .setState(Constant.STATE_PENDING));//记录tracking到db
                return task;
            }
        }
        logger.error("getNextStep task|task_id|step|offer_id is null");
        return null;
    }

    @Transactional
    public void taskError(String body) {
        JsonNode request;
        try {
            request = objectMapper.readTree(body);
        } catch (IOException e) {
            logger.error("task error parse string to json fail", e);
            return;
        }
        if (request.has("task")) {
            String tracks = request.has("tracks") ? request.get("tracks").toString() : null;
            String client = request.has("client") ? request.get("client").toString() : null;
            JsonNode taskNode = request.get("task");
            String taskId = taskNode.has("task_id") ? taskNode.get("task_id").asText() : null;
            Integer step = taskNode.has("step") ? taskNode.get("step").asInt() : null;
            Integer offerId = taskNode.has("offer_id") ? taskNode.get("offer_id").asInt() : null;
            String errorType = request.has("error_type") ? request.get("error_type").asText() : null;
            String errorMsg = request.has("error_message") ? request.get("error_message").asText() : null;
            if (ObjectUtils.allNotNull(taskId, step, offerId)) {
                taskTrackingDao.insertTaskTracking(new TaskTracking()
                        .setTask_id(taskId)
                        .setStep(step)
                        .setOffer_id(offerId)
                        .setDevice_info(client)
                        .setTracks(tracks)
                        .setState(Constant.STATE_FAIL));//更新tracks到db
                taskErrorDao.insertTaskError(new TaskError()
                        .setTask_id(taskId)
                        .setStep(step)
                        .setOffer_id(offerId)
                        .setError_type(errorType)
                        .setError_msg(errorMsg)
                        .setDevice_info(client)
                        .setTracks(tracks));
                return;
            }
        }
        logger.error("taskError task|task_id|step|offer_id is null");
    }

    private Tuple2<String, String> getUserIdFromBody(String body) {
        JsonNode request = null;
        try {
            request = objectMapper.readTree(body);
        } catch (IOException e) {
            logger.error("getUserIdFromBody parse string to json fail", e);
        }
        if (request != null && request.has("client")) {
            JsonNode client = request.get("client");
            String userId = null;
            if (client.has("gaid")) {
                userId = client.get("gaid").asText();
            } else if (client.has("android_id")) {
                userId = client.get("android_id").asText();
            }
            return new Tuple2<>(userId, client.toString());
        }
        return new Tuple2<>();
    }
}
