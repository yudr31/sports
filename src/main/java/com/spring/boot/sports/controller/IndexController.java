package com.spring.boot.sports.controller;

import com.spring.boot.common.annotation.validation.ValidationExecutor;
import com.spring.boot.common.bean.BaseController;
import com.spring.boot.common.bean.ResponseData;
import com.spring.boot.feign.pojo.sports.LoginUser;
import com.spring.boot.sports.dto.LoginUserDTO;
import com.spring.boot.sports.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author yuderen
 * version 2018/11/20 14:00
 */
@RestController
public class IndexController extends BaseController {

    @Autowired
    private LoginUserService loginUserService;

    @RequestMapping("/login")
    public ResponseData<String> login(@RequestBody LoginUser loginUser) {
        String[] fields = {"userName","password"};
        ValidationExecutor.notNullValidate(fields,loginUser);
        LoginUser result = loginUserService.login(loginUser);
        return successResponse(result.getGid());
    }

    @RequestMapping("/logout")
    public ResponseData<String> logout() {
        loginUserService.logout();
        return successResponse(1);
    }

    @RequestMapping("/userInfo")
    public ResponseData<LoginUser> userInfo() {
        return successResponse(loginUserService.userInfo());
    }

    @RequestMapping("/resetPassword")
    public ResponseData<LoginUser> resetPassword(@RequestBody LoginUserDTO loginUserDTO) {
        String[] fields = {"oldPassword","password"};
        ValidationExecutor.notNullValidate(fields,loginUserDTO);
        return successResponse(loginUserService.resetPassword(loginUserDTO));
    }
}
