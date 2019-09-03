package com.solid.subscribe.web.service;

import com.alibaba.fastjson.JSON;
import com.solid.subscribe.web.dao.OfferDao;
import com.solid.subscribe.web.entity.Offer;
import com.solid.subscribe.web.utils.ConvBeanUtil;
import com.solid.subscribe.web.vo.offer.OfferVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class OfferService {
    @Autowired
    OfferDao offerDao;

    public List<Offer> getOfferList() {
        return offerDao.selectOfferList();
    }

    public Boolean switchStatus(Integer id, Integer status) {
        return offerDao.updateOfferStatus(id, status) > 0;
    }

    public OfferVO getOffer(Integer id) {
        OfferVO offerVO = new OfferVO();
        Offer offer = offerDao.selectOffer(id);
        ConvBeanUtil.conv(offer, offerVO);
        offerVO.setTarget_country(strToList(offer.getTarget_country()));
        offerVO.setTarget_os(strToList(offer.getTarget_os()));
        offerVO.setTarget_operator(strToList(offer.getTarget_operator()));
        return offerVO;
    }

    private List<String> strToList(String str){
        if (null != str){
            Integer first = str.indexOf("[") + 1;
            Integer last = str.indexOf("]") > -1 ? str.indexOf("]") : str.length();
            return Arrays.asList(str.substring(first, last).split(","));
        }
        return new LinkedList<String>();
    }

    public Boolean createOffer(OfferVO offerVO) {
        if (offerDao.insertOffer(buildOffer(offerVO)) > 0) {
            return true;
        }
        return false;
    }

    public Boolean editOffer(OfferVO offerVO) {
        if (offerDao.updateOffer(buildOffer(offerVO)) > 0) {
            return true;
        }
        return false;
    }

    private Offer buildOffer(OfferVO offerVO){
        Offer offer = new Offer();
        ConvBeanUtil.conv(offerVO, offer);
        offer.setTarget_os(JSON.toJSONString(offerVO.getTarget_os()));
        offer.setTarget_country(JSON.toJSONString(offerVO.getTarget_country()));
        offer.setTarget_operator(JSON.toJSONString(offerVO.getTarget_operator()));
        return offer;
    }
}
