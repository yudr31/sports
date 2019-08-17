package com.spring.boot.sports.controller;

import com.github.pagehelper.PageInfo;
import com.spring.boot.common.annotation.validation.ValidationExecutor;
import com.spring.boot.common.bean.BaseController;
import com.spring.boot.common.bean.ResponseData;
import com.spring.boot.feign.pojo.sports.NotifyAccount;
import com.spring.boot.feign.pojo.sports.OuterUser;
import com.spring.boot.sports.service.NotifyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * author yuderen
 * version 2018/11/20 19:57
 */
@RestController
@RequestMapping("/notifyAccount")
public class NotifyAccountController extends BaseController {

    @Autowired
    private NotifyAccountService notifyAccountService;

    @RequestMapping("/notifyAccountList")
    public ResponseData<PageInfo<OuterUser>> notifyAccountList(NotifyAccount notifyAccount){
        return successResponse(notifyAccountService.fetchRecordPageInfo(notifyAccount));
    }

    @RequestMapping("/addNotifyAccount")
    public ResponseData<Integer> addNotifyAccount(NotifyAccount notifyAccount){
        String[] fields = {"userName","phone","verifyCode"};
        ValidationExecutor.notNullValidate(fields,notifyAccount);
        Integer count = notifyAccountService.insertSelective(notifyAccount);
        return count == 0 ? errorResponse("添加失败！") : successResponse(count);
    }

    @RequestMapping("/editNotifyAccount")
    public ResponseData<Integer> editNotifyAccount(NotifyAccount notifyAccount){
        String[] fields = {"gid","userName","phone"};
        ValidationExecutor.notNullValidate(fields,notifyAccount);
        Integer count = notifyAccountService.updateSelectiveByKey(notifyAccount);
        return count == 0 ? errorResponse("更新失败！") : successResponse(count);
    }

    @RequestMapping("/removeNotifyAccount")
    public ResponseData<Integer> removeNotifyAccount(NotifyAccount notifyAccount){
        String[] fields = {"gid"};
        ValidationExecutor.notNullValidate(fields,notifyAccount);
        Integer count = notifyAccountService.removeRecord(notifyAccount.getGid());
        return count == 0 ? errorResponse("移除失败") : successResponse(count);
    }

}
