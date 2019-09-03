package com.solid.subscribe.web.controller;

import com.solid.subscribe.web.entity.Offer;
import com.solid.subscribe.web.perm.util.shiro.ShiroConstants;
import com.solid.subscribe.web.service.OfferService;
import com.solid.subscribe.web.vo.Response;
import com.solid.subscribe.web.vo.SwitchVO;
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

    @RequiresPermissions(ShiroConstants.OFFER)
    @RequestMapping(value="listView")
    public String permUser(){
        return "subscribe/offer/offerList";
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @GetMapping(value = "createView")
    public ModelAndView createBidder() {
        ModelAndView model = new ModelAndView();
        model.setViewName("subscribe/offer/createView");
        return model;
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @GetMapping(value = "editView")
    public ModelAndView editBidder(Integer bidderid) {
        ModelAndView model = new ModelAndView();
        model.setViewName("subscribe/offer/editView");
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
/*

    @RequiresPermissions(ShiroConstants.OFFER)
    @PostMapping("new")
    public Response createBidder(@Valid @RequestBody DemandVO bidderVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.getInstance().error(ProcessValidUtil.errorMsg(bindingResult));
        }
        if (demandService.keyIsExist(bidderVO.getId())) {
            return Response.keyExistError();
        }
        if (demandService.createBidder(bidderVO)) {
            return Response.success();
        }
        return Response.error();
    }

    @RequiresPermissions(ShiroConstants.OFFER)
    @GetMapping("get")
    public Response getBidder(Integer id) {
        return Response.getInstance().success(demandService.getBidderVO(id));
    }

    @RequiresPermissions(ShiroConstants.BIDDER_LIST)
    @PostMapping("edit")
    public Response editBidder(@Valid @RequestBody DemandVO bidderVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.getInstance().error(ProcessValidUtil.errorMsg(bindingResult));
        }
        if (demandService.editBidder(bidderVO)) {
            return Response.success();
        }
        return Response.error();
    }*/

    @RequiresPermissions(ShiroConstants.OFFER)
    @PostMapping("switch")
    @ResponseBody
    public Response switchStatus(@Valid @RequestBody SwitchVO switchVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder messages = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) { // 获取与给定字段关联的错误
                messages.append(fieldError.getDefaultMessage());
                messages.append("; ");
            }
            return Response.getInstance().error(messages.toString());
        }
        if (offerService.switchStatus(switchVO.getId(), switchVO.getStatus())) {
            return Response.success();
        }
        return Response.error();
    }

}
