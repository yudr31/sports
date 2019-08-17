package com.spring.boot.sports.controller;

import com.github.pagehelper.PageInfo;
import com.spring.boot.common.bean.BaseController;
import com.spring.boot.common.bean.ResponseData;
import com.spring.boot.feign.pojo.sports.OrderInfo;
import com.spring.boot.sports.service.OrderInfoService;
import com.spring.boot.sports.service.QuyundongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author yuderen
 * version 2018/11/23 11:47
 */
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController extends BaseController {

    @Autowired
    private OrderInfoService notifyAccountService;

    @RequestMapping("/orderInfoList")
    public ResponseData<PageInfo<OrderInfo>> orderInfoList(OrderInfo orderInfo){
        return successResponse(notifyAccountService.fetchRecordPageInfo(orderInfo));
    }

    @RequestMapping("/orderInfoSortList")
    public ResponseData<PageInfo<OrderInfo>> orderInfoSortList(OrderInfo orderInfo){
        return successResponse(notifyAccountService.fetchRecordSortList(orderInfo));
    }

    @Autowired
    private QuyundongService quyundongService;

    @RequestMapping("/test")
    public ResponseData<String> test(){
        return successResponse(quyundongService.getData());
    }

    @RequestMapping("/testMsgNotify")
    public ResponseData<String> testMsgNotify(){
        quyundongService.msgNotify();
        return successResponse("ok");
    }

}
