package com.spring.boot.sports.interceptor;

import com.spring.boot.common.exception.BaseException;
import com.spring.boot.common.util.SpringContextUtil;
import com.spring.boot.sports.service.UserManagerService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * author yuderen
 * version 2018/11/23 14:30
 */
@Aspect
@Order(100)
@Component
public class LoginInterceptor {

    @Autowired
    private UserManagerService userManagerService;

    @Before("execution(public * com.spring.boot.*.controller.*.*(..))")
    public void userOnline(){
        loginCheck();
    }

    private void loginCheck(){
        HttpServletRequest request = SpringContextUtil.getRequest();
        String uri = request.getRequestURI();
        if ("/sports/login".equals(uri)){
            return;
        }
        if ("/login".equals(uri)){
            return;
        }
        Boolean login = userManagerService.checkUserLogin();
        if (login){
            return;
        }
//        throw new BaseException("1","用户未登录");
    }

}
