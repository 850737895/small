package com.small.common;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 读取cookie的工具类
 * Created by 85073 on 2018/5/30.
 */
public class CookieUtil {

    private static final String COOKIE_DOMAIN = ".small.com";

    private static final String COOKIE_PATH = "/";

    private static final String COOKIE_NAME = "small_cookie_name";
    /**cookie有效时间*/
    private static final Integer COOKIE_EXPIRE_TIME = 60*60*24;
    /**刪除cookie*/
    private static final Integer COOKIE_DEL_FLAG = 0;

    /**
     * 读取cookie信息
     * @param request request
     * @return  String
     */
    public static String readCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(null == cookies || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie: cookies) {
            String cookieName = cookie.getName();
            if(StringUtils.equals(cookieName,COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    };

    /**
     * 写cookie信息
     * @param response response
     * @param token JsessionId
     */
    public static void writeCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        //设置cookie的有效时间为一天
        cookie.setMaxAge(COOKIE_EXPIRE_TIME);
        //设置cookie的路径
        cookie.setPath(COOKIE_PATH);
        //设置cookie的域名
        cookie.setDomain(COOKIE_DOMAIN);
        //设置防止脚本获取用户的cookie的信息
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     *刪除cookie
     * @param request request
     * @param response response
     */
    public static void delCookie(HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0) {
            for (Cookie cookie: cookies) {
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)) {
                    cookie.setMaxAge(COOKIE_DEL_FLAG);
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
