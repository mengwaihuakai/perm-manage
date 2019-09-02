package com.solid.subscribe.service;

import com.alibaba.fastjson.JSON;
import com.solid.subscribe.dao.OfferDao;
import com.solid.subscribe.entity.Offer;
import com.solid.subscribe.targeting.Engine;
import com.solid.subscribe.targeting.EngineBuilder;
import com.solid.subscribe.vo.OfferVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author fanyongju
 * @Title: OfferService
 * @date 2019/8/27 11:42
 */
@Service
public class OfferService {
    @Autowired
    private OfferDao offerDao;
    private OfferSnapshot offerSnapshot;

    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);

    @PostConstruct
    public void init() {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("OfferData-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(this::applyOfferData, 0, 1, TimeUnit.MINUTES);
    }

    public final List<OfferVo.Data> findOffers(Map<String, String> trafficProperties) {
        final OfferSnapshot ss = getBidderSnapshot();
        if (ss != null) {
            Map<Integer, Integer> offerIds = ss.getTargetingEngine().target(trafficProperties != null ? trafficProperties : new HashMap<>(), false, 1000, null);
            if (offerIds != null) {
                Map<Integer, OfferVo.Data> offers = ss.getOffers();
                return offerIds.keySet().stream().map(offers::get).filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    public synchronized final OfferSnapshot getBidderSnapshot() {
        return this.offerSnapshot;
    }

    public Map<Integer, OfferVo.Data> getOfferIds(Map<String, String> trafficProperties) {
        return findOffers(trafficProperties).stream().collect(Collectors.toMap(OfferVo.Data::getOffer_id, e -> e));
    }

    private OfferVo convOfferToOfferVo(Offer offer) {
        return OfferVo.builder()
                .offer(OfferVo.Data.builder()
                        .offer_id(offer.getOffer_id())
                        .url(offer.getUrl())
                        .isNotificationEnabled(offer.getIs_notification_enabled())
                        .isCloseWifi(offer.getIs_close_wifi())
                        .budget(offer.getBudget())
                        .dailyBudget(offer.getDaily_budget())
                        .build())
                .targeting(new HashMap<String, String>() {{
                    put("country", buildValueByMode(offer.getTarget_country_mode(), offer.getTarget_country()));
                }}).build();
    }

    private String buildValueByMode(Integer mode, String value) {
        StringBuilder resultValue = new StringBuilder();
        if (mode.equals(1) && StringUtils.isNotEmpty(value)) {
            resultValue.append(StringUtils.join(JSON.parseObject(value, List.class), ","));
        } else if (mode.equals(2) && StringUtils.isNotEmpty(value)) {
            resultValue.append("*:1,");
            List<String> valueList = (List<String>) JSON.parseObject(value, List.class).stream().map(e -> e + ":0").collect(Collectors.toList());
            resultValue.append(StringUtils.join(valueList, ","));
        } else if (mode.equals(0)) {
            resultValue.append("*");
        }
        return resultValue.toString();
    }

    private void applyOfferData() {
        try {
            List<OfferVo> offerVoList = offerDao.selectAllActiveOffer().stream().map(this::convOfferToOfferVo).collect(Collectors.toList());
            EngineBuilder engineBuilder = EngineBuilder.newBuilder();
            engineBuilder.addTag("country", false, false);
            Map<Integer, OfferVo.Data> offerMap = new HashMap<>();
            for (int i = 0; i < offerVoList.size(); i++) {
                OfferVo offerVo = offerVoList.get(i);
                offerMap.put(offerVo.getOffer().getOffer_id(), offerVo.getOffer());
                engineBuilder.addData(offerVo.getOffer().getOffer_id(), offerVo.getOffer().getOffer_id().toString() + "-name", offerVo.getTargeting() != null ? offerVo.getTargeting() : new HashMap<>());
            }
            final Engine engine = engineBuilder.build();
            setOfferSnapshot(new OfferSnapshot(offerMap, engine));
        } catch (Exception e) {
            logger.warn("Check offers data got Exception:", e);
        }
    }

    private synchronized void setOfferSnapshot(OfferSnapshot newSnapshot) {
        logger.info("Update offers snapshot: {} -> {}", this.offerSnapshot != null ? this.offerSnapshot.getOffers().size() : 0, newSnapshot.getOffers().size());
        this.offerSnapshot = newSnapshot;
    }

    public static class OfferSnapshot {
        private final long timestamp;
        private final Map<Integer, OfferVo.Data> offers;
        private final Engine targetingEngine;

        public OfferSnapshot(Map<Integer, OfferVo.Data> offers, Engine targetingEngine) {
            this.timestamp = System.currentTimeMillis();
            this.offers = offers;
            this.targetingEngine = targetingEngine;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Map<Integer, OfferVo.Data> getOffers() {
            return offers;
        }

        public Engine getTargetingEngine() {
            return targetingEngine;
        }
    }
}
