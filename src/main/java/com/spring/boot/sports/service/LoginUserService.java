package com.spring.boot.sports.service;

import com.spring.boot.common.bean.BaseBeanService;
import com.spring.boot.common.exception.BaseException;
import com.spring.boot.feign.pojo.sports.LoginUser;
import com.spring.boot.sports.dto.LoginUserDTO;
import com.spring.boot.sports.mapper.LoginUserMapper;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author yuderen
 * version 2018/11/20 13:59
 */
@Service
public class LoginUserService extends BaseBeanService<LoginUserMapper, LoginUser> {

    private static final Integer HASH_COUNT = 3;
    private static final String AUTH_NAME = "MD5";

    @Autowired
    private LoginUserMapper loginUserMapper;
    @Autowired
    private UserManagerService userManagerService;


    public LoginUser login(LoginUser loginUser){
        LoginUser result = loginUserMapper.fetchLoginUserByUserName(loginUser.getUserName());
        if (null == result){
            throw new BaseException("10001","用户不存在");
        }
        Object password = new SimpleHash(AUTH_NAME,loginUser.getPassword(), result.getSaltRandom(),HASH_COUNT);
        if (!result.getPassword().equals(password.toString())){
            throw new BaseException("10002","密码错误");
        }
        userManagerService.saveLoginUserToMap(result);
        return result;
    }

    public void logout(){
        userManagerService.removeLoginUser();
    }

    public LoginUser userInfo(){
        Long userId = userManagerService.getLoginUserId();
        LoginUser loginUser = fetchRecordByGid(userId);
        if (null != loginUser){
            loginUser.setPassword("");
            loginUser.setSaltRandom("");
        }
        return loginUser;
    }

    public Integer resetPassword(LoginUserDTO loginUserDTO){
        Long userId = userManagerService.getLoginUserId();
        LoginUser result = fetchRecordByGid(userId);
        if (null == result){
            throw new BaseException("10001","用户不存在");
        }
        Object oldPassword = new SimpleHash(AUTH_NAME,loginUserDTO.getOldPassword(), result.getSaltRandom(),HASH_COUNT);
        if (!result.getPassword().equals(oldPassword.toString())){
            throw new BaseException("10002","密码错误");
        }
        Object password = new SimpleHash(AUTH_NAME,loginUserDTO.getPassword(), result.getSaltRandom(),HASH_COUNT);
        result.setPassword(password.toString());
        return updateSelectiveByKey(result);
    }

}
