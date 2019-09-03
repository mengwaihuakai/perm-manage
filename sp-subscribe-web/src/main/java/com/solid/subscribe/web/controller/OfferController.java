package com.solid.subscribe.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.solid.subscribe.web.entity.Offer;
import com.solid.subscribe.web.perm.util.shiro.ShiroConstants;
import com.solid.subscribe.web.service.CountryService;
import com.solid.subscribe.web.service.OfferService;
import com.solid.subscribe.web.utils.ProcessValidUtil;
import com.solid.subscribe.web.vo.Response;
import com.solid.subscribe.web.vo.SwitchVO;
import com.solid.subscribe.web.vo.offer.OfferVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "subscribe/offer")
public class OfferController {
    @Autowired
    OfferService offerService;
    @Autowired
    CountryService countryService;

    @RequiresPermissions(ShiroConstants.OFFER)
    @RequestMapping(value="listView")
    public String permUser(){
        return "subscribe/offer/offerList";
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @GetMapping(value = "createView")
    @ResponseBody
    public ModelAndView createOffer() {
        ModelAndView model = new ModelAndView();
        model.addObject("country", JSON.toJSONString(countryService.selectAllCountry(), SerializerFeature.UseSingleQuotes));
        model.setViewName("subscribe/offer/createOffer");
        return model;
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @GetMapping(value = "editView")
    @ResponseBody
    public ModelAndView editOffer(Integer id) {
        ModelAndView model = new ModelAndView();
        model.addObject("country", JSON.toJSONString(countryService.selectAllCountry(), SerializerFeature.UseSingleQuotes));
        model.addObject("editValue", JSON.toJSONString(offerService.getOffer(id)));
        model.setViewName("subscribe/offer/editOffer");
        return model;
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @PostMapping("search")
    @ResponseBody
    public Response search() {
        List<Offer> offerList = offerService.getOfferList();
        Response response = Response.getInstance().success(offerList);
        return response;
    }


    @RequiresPermissions(ShiroConstants.OFFER)
    @PostMapping("new")
    @ResponseBody
    public Response createBidder(@Valid @RequestBody OfferVO offerVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.getInstance().error(ProcessValidUtil.errorMsg(bindingResult));
        }
        if (offerService.createOffer(offerVO)) {
            return Response.success();
        }
        return Response.error();
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @PostMapping("edit")
    @ResponseBody
    public Response editBidder(@Valid @RequestBody OfferVO offerVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.getInstance().error(ProcessValidUtil.errorMsg(bindingResult));
        }
        if (offerService.editOffer(offerVO)) {
            return Response.success();
        }
        return Response.error();
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @PostMapping("switch")
    @ResponseBody
    public Response switchStatus(@Valid @RequestBody SwitchVO switchVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.getInstance().error(ProcessValidUtil.errorMsg(bindingResult));
        }
        if (offerService.switchStatus(switchVO.getId(), switchVO.getStatus())) {
            return Response.success();
        }
        return Response.error();
    }

}
