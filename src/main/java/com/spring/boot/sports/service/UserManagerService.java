package com.spring.boot.sports.service;

import com.spring.boot.common.util.Base64Util;
import com.spring.boot.common.util.CookieUtils;
import com.spring.boot.feign.pojo.sports.LoginUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author yuderen
 * version 2018/11/20 15:31
 */
@Slf4j
@Service
public class UserManagerService {

    public static final String AUTH_KEY = "auth_key";
    public static final String AUTH_LOGIN_TOKEN = "auth_login_token";  //cookie author key
    public static final Integer TOKEN_EXPIRY_TIME = 3*10*60;

    private static Map<String, UnifiedUser> sessionMap = new ConcurrentHashMap();

    /**
     * 保存登录authLoginToken信息到Cookie
     * @param loginUser
     */
    private String responseAuthLoginToken(LoginUser loginUser) {
        String authToken = setAuthToken(loginUser);
        CookieUtils.addCookie(AUTH_LOGIN_TOKEN,authToken, TOKEN_EXPIRY_TIME,"");
        return authToken;
    }

    public UnifiedUser saveLoginUserToMap(LoginUser loginUser){
        String authToken = responseAuthLoginToken(loginUser);
        UnifiedUser unifiedUser = new UnifiedUser();
        unifiedUser.setUserId(loginUser.getGid());
        unifiedUser.setUserName(loginUser.getUserName());
        unifiedUser.setLoggedIn(true);
        unifiedUser.setActiveTime(LocalDateTime.now());
        return sessionMap.put(authToken, unifiedUser);
    }

    public Boolean checkUserLogin(){
        String token = getCookieString();
        if (StringUtils.isEmpty(token)){
            return false;
        }
        UnifiedUser loginUser = getNotExpiryTimeLoginUser(token);
        return null != loginUser;
    }

    public UnifiedUser getLoginUser(){
        String authToken = getCookieString();
        if (StringUtils.isEmpty(authToken)){
            return null;
        }
        return sessionMap.get(authToken);
    }

    public Long getLoginUserId(){
        UnifiedUser unifiedUser = getLoginUser();
        if (null == unifiedUser){
            return null;
        }
        return unifiedUser.getUserId();
    }

    public UnifiedUser getNotExpiryTimeLoginUser(String authToken){
        UnifiedUser unifiedUser = sessionMap.get(authToken);
        if (null != unifiedUser){
            if (LocalDateTime.now().compareTo(unifiedUser.getActiveTime().plusSeconds(TOKEN_EXPIRY_TIME)) > 0){
                return null;
            }
        }
        return unifiedUser;
    }

    public UnifiedUser removeLoginUser(){
        String authToken = getCookieString();
        System.out.println(authToken);
        return sessionMap.remove(authToken);
    }

    private String setAuthToken(LoginUser loginUser){
        return Base64Util.encode(loginUser.getGid() + "|" + loginUser.getUserName());
    }

    public String getCookieString(){
        return CookieUtils.getCookieString(AUTH_LOGIN_TOKEN);
    }

    @Data
    public class UnifiedUser extends LoginUser {
        private Long userId;
        private Boolean loggedIn;
        private LocalDateTime activeTime;
    }

}
