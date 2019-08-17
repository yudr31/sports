package com.spring.boot.sports.service;

import com.spring.boot.common.bean.BaseBeanService;
import com.spring.boot.feign.pojo.sports.NotifyAccount;
import com.spring.boot.sports.mapper.NotifyAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author yuderen
 * version 2018/11/22 16:26
 */
@Service
public class NotifyAccountService extends BaseBeanService<NotifyAccountMapper, NotifyAccount> {

    @Autowired
    private NotifyAccountMapper notifyAccountMapper;

}