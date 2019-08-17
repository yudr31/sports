package com.spring.boot.sports.controller;

import com.github.pagehelper.PageInfo;
import com.spring.boot.common.annotation.validation.ValidationExecutor;
import com.spring.boot.common.bean.BaseController;
import com.spring.boot.common.bean.ResponseData;
import com.spring.boot.feign.pojo.sports.BookingInfo;
import com.spring.boot.feign.pojo.sports.OuterUser;
import com.spring.boot.sports.service.BookingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * author yuderen
 * version 2018/11/20 19:57
 */
@RestController
@RequestMapping("/bookingInfo")
public class BookingInfoController extends BaseController {

    @Autowired
    private BookingInfoService bookingInfoService;

    @RequestMapping("/bookingInfoList")
    public ResponseData<PageInfo<OuterUser>> bookingInfoList(BookingInfo bookingInfo){
        return successResponse(bookingInfoService.fetchRecordPageInfo(bookingInfo));
    }

    @RequestMapping("/addBookingInfo")
    public ResponseData<Integer> addBookingInfo(BookingInfo bookingInfo){
        String[] fields = {"bookingTime","duration","bookingDate","courtType","orderNow","notifyType","phone"};
        ValidationExecutor.notNullValidate(fields,bookingInfo);
        Integer count = bookingInfoService.insertSelective(bookingInfo);
        return count == 0 ? errorResponse("添加失败！") : successResponse(count);
    }

    @RequestMapping("/editBookingInfo")
    public ResponseData<Integer> editBookingInfo(BookingInfo bookingInfo){
        String[] fields = {"bookingTime","duration","bookingDate","courtType","orderNow","notifyType","phone"};
        ValidationExecutor.notNullValidate(fields,bookingInfo);
        Integer count = bookingInfoService.updateSelectiveByKey(bookingInfo);
        return count == 0 ? errorResponse("更新失败！") : successResponse(count);
    }

    @RequestMapping("/updateBookingState")
    public ResponseData<Integer> updateBookingState(BookingInfo bookingInfo){
        String[] fields = {"gid","bookingState"};
        ValidationExecutor.notNullValidate(fields,bookingInfo);
        Integer count = bookingInfoService.updateBookingState(bookingInfo);
        return count == 0 ? errorResponse("更新失败！") : successResponse(count);
    }

    @RequestMapping("/bookingInfoDetail")
    public ResponseData<BookingInfo> bookingInfoDetail(BookingInfo bookingInfo){
        String[] fields = {"gid"};
        ValidationExecutor.notNullValidate(fields,bookingInfo);
        BookingInfo record = bookingInfoService.fetchRecordByGid(bookingInfo.getGid());
        return successResponse(record);
    }

    @RequestMapping("/removeBookingInfo")
    public ResponseData<Integer> removeBookingInfo(BookingInfo bookingInfo){
        String[] fields = {"gid"};
        ValidationExecutor.notNullValidate(fields,bookingInfo);
        Integer count = bookingInfoService.removeRecord(bookingInfo.getGid());
        return count == 0 ? errorResponse("删除失败！") : successResponse(count);
    }

}
