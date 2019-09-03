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
import java.util.Objects;
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

    private static ExpiringMap<String, String> clientOfferMap = ExpiringMap.builder().expiration(5, TimeUnit.MINUTES)//测试时先用五分钟
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();//失效时间为一天

    private ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public OfferRspVo getOffers(String body, HttpServletRequest req) {
        GeoIp.GeoInfo geoInfo = geoIp.findGeoInfoByRequest(req);
        if (geoInfo == null || StringUtils.isEmpty(geoInfo.getCountry())) {
            logger.error("geoinfo is null");
            return null;
        }
        JsonNode request = parseBody(body);
        if (request != null && request.has("client")) {
            JsonNode client = request.get("client");
            String userId = null;
            if (client.has("android_id")) {
                userId = client.get("android_id").asText();
            } else if (client.has("gaid")) {
                userId = client.get("gaid").asText();
            }
            boolean isNotificationEnabled = client.has("isNotificationEnabled") && client.get("isNotificationEnabled").asBoolean();
            String bundle = client.has("app_pkg") ? client.get("app_pkg").asText() : null;

            Map<Integer, OfferVo.Data> offerVoMap = offerService.getOfferIds(new HashMap<String, String>() {{
                put("country", geoInfo.getCountry());
                put("os", "android");
            }});
            List<Integer> offerIds = new ArrayList<>(offerVoMap.keySet());
            List<OfferRspVo.Task> tasks = new ArrayList<>();
            Random random = new Random();
            while (tasks.size() < 1 && offerIds.size() > 0) {
                Integer offerId = offerIds.get(random.nextInt(offerIds.size()));//随机选择一个offerId
                offerIds.remove(offerId);//选择后就从list中移除，防止重复选择
                OfferVo.Data offer = offerVoMap.get(offerId);
                if (clientOfferMap.containsKey(offerId + ":" + userId)) {//如果一个小时内offer已经推送给该userId
                    logger.info("the offer {} has been pushed to the device {} within an hour", offerId, userId);
                    continue;
                }
                if (!isNotificationEnabled && offer.getIsNotificationEnabled().equals(1)) {//设备不支持通知，且该offer要求支持通知
                    logger.info("The device {} does not support notifications, and the offer {} requires support notifications", userId, offerId);
                    continue;
                }
                /*if (taskTrackingService.hasSuccess(offerId, userId)) {//如果该userId已经成功执行该offer
                    logger.info("the device {} has successfully executed the offer {}", userId, offerId);
                    continue;
                }*/
                if (taskTrackingService.budgetNotEnough(offer)) {//预算不足
                    logger.info("offer {} total budget is not enough", offerId);
                    continue;
                }
                TaskSteps taskSteps = taskStepsDao.selectFirstStepRecordByOfferId(offerId);//获取第一步任务
                if (taskSteps == null) {//该offerid没有step存在
                    logger.info("offer {} no step exists", offerId);
                    continue;
                }
                String taskId = IdWorker.generateId().toString();
                OfferRspVo.Task task = new OfferRspVo.Task()
                        .setOffer_id(offerId)
                        .setStep(taskSteps.getStep())
                        .setTask_id(taskId)
                        .setUrl(taskSteps.getUrl().replace("{TASK_ID}", taskId))
                        .setIsCloseWifi(offerVoMap.get(offerId).getIsCloseWifi())
                        .setType(taskSteps.getType())
                        .setNext_on(taskSteps.getNext_on())
                        .setJs(taskSteps.getJs());
                tasks.add(task);
                taskTrackingDao.insertTaskTracking(new TaskTracking()
                        .setTask_id(task.getTask_id())
                        .setStep(task.getStep())
                        .setOffer_id(offerId)
                        .setCountry(geoInfo.getCountry())
                        .setUser_id(userId)
                        .setBundle(bundle)
                        .setDevice_info(client.toString())
                        .setState(Constant.STATE_PENDING));//记录tracking到db
                clientOfferMap.put(offerId + ":" + userId, "value");//记录下该userid与offerid的关联关系
            }
            return new OfferRspVo().setInterval(10).setTasks(tasks);
        }
        logger.error("get offers request body is not format");
        return null;
    }

    public OfferRspVo.Task getNextStep(String body, HttpServletRequest req) {
        GeoIp.GeoInfo geoInfo = geoIp.findGeoInfoByRequest(req);
        JsonNode request = parseBody(body);
        if (request != null && request.has("task")) {
            String tracks = request.has("tracks") ? request.get("tracks").toString() : null;
            JsonNode client = request.has("client") ? request.get("client") : null;
            JsonNode taskNode = request.get("task");
            String taskId = taskNode.has("task_id") ? taskNode.get("task_id").asText() : null;
            Integer step = taskNode.has("step") ? taskNode.get("step").asInt() : null;
            Integer offerId = taskNode.has("offer_id") ? taskNode.get("offer_id").asInt() : null;
            String userId = null;
            String bundle = null;
            if (client != null) {
                if (client.has("android_id")) {
                    userId = client.get("android_id").asText();
                } else if (client.has("gaid")) {
                    userId = client.get("gaid").asText();
                }
                bundle = client.has("app_pkg") ? client.get("app_pkg").asText() : null;
            }
            if (ObjectUtils.allNotNull(taskId, step, offerId)) {
                taskTrackingDao.insertTaskTracking(new TaskTracking()
                        .setTask_id(taskId)
                        .setStep(step)
                        .setOffer_id(offerId)
                        .setDevice_info(client != null ? client.toString() : null)
                        .setTracks(tracks)
                        .setState(Constant.STATE_SUCCESS));//更新tracks到db
                TaskSteps taskSteps = taskStepsDao.selecNextStep(offerId, step);
                if (taskSteps == null) {//没有下一步任务，返回释放任务
                    return new OfferRspVo.Task()
                            .setOffer_id(offerId)
                            .setStep(step + 1)
                            .setTask_id(taskId)
                            .setType("end");
                }
                taskTrackingDao.insertTaskTracking(new TaskTracking()
                        .setTask_id(taskId)
                        .setStep(taskSteps.getStep())
                        .setOffer_id(offerId)
                        .setCountry(Objects.requireNonNull(geoInfo).getCountry())
                        .setBundle(bundle)
                        .setUser_id(userId)
                        .setDevice_info(client != null ? client.toString() : null)
                        .setState(Constant.STATE_PENDING));//记录tracking到db
                return new OfferRspVo.Task()
                        .setOffer_id(offerId)
                        .setStep(taskSteps.getStep())
                        .setTask_id(taskId)
                        .setUrl(taskSteps.getUrl().replace("{TASK_ID}", taskId))
                        .setType(taskSteps.getType())
                        .setNext_on(taskSteps.getNext_on())
                        .setJs(taskSteps.getJs());
            }
        }
        logger.error("get next step request body is not format");
        return null;
    }

    @Transactional
    public void taskError(String body) {
        JsonNode request = parseBody(body);
        if (request != null && request.has("task")) {
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
        logger.error("task error request body is not format");
    }

    private JsonNode parseBody(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (IOException e) {
            logger.error("parse string to json fail", e);
        }
        return null;
    }
}
