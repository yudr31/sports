package com.spring.boot.sports.service;

import com.spring.boot.common.bean.BaseBeanService;
import com.spring.boot.feign.pojo.sports.OuterUser;
import com.spring.boot.sports.mapper.OuterUserMapper;
import org.springframework.stereotype.Service;

/**
 * author yuderen
 * version 2018/11/20 19:54
 */
@Service
public class OuterUserService extends BaseBeanService<OuterUserMapper, OuterUser> {



}
