package com.solid.subscribe.web.service;

import com.solid.subscribe.web.dao.OfferDao;
import com.solid.subscribe.web.entity.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
