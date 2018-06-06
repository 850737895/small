package com.small.interceptors;

import com.google.common.collect.Maps;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.pojo.User;
import com.small.utils.CookieUtil;
import com.small.utils.JsonUtil;
import com.small.utils.RedisShardingPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 普通权限验证
 * Created by 85073 on 2018/6/6.
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("AuthInterceptor.preHandle()方法");
        HandlerMethod handlerMethod = (HandlerMethod) o;
        //解析类名
        String className = handlerMethod.getBean().getClass().getSimpleName();
        //解析方法名
        String methodName = handlerMethod.getMethod().getName();

        Map<String,String[]> paramMap = httpServletRequest.getParameterMap();

        Set<String> keySet = paramMap.keySet();

        Iterator<String> iterator = keySet.iterator();

        //解析参数,具体的参数key以及value是什么，我们打印日志
        StringBuffer requestParamBuffer = new StringBuffer();

        Map<String,Object> returnMap = Maps.newHashMap();

        if(StringUtils.equals(className,"UserController") && StringUtils.equals(methodName,"login")) {
            //防止打印登陆日志 会暴露结果,登陆也不需要拦截
            log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            return true;
        }

        while (iterator.hasNext()) {
            String key = iterator.next();
            String [] values = paramMap.get(key);
            String valuesStr = Arrays.asList(values).toString();
            requestParamBuffer.append(key).append("=").append(valuesStr);
        }
        log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,requestParamBuffer);

        String token = CookieUtil.readCookie(httpServletRequest);
        User user = null;
        if(StringUtils.isNotEmpty(token)) {
            String userStr = RedisShardingPoolUtil.get(token);
            if(StringUtils.isNotEmpty(userStr)) {
                user = JsonUtil.str2Obj(userStr,User.class);
            }
        }

        if(user == null) {

            httpServletResponse.reset();

            httpServletResponse.setCharacterEncoding("UTF-8");

            httpServletResponse.setContentType("application/json");

            PrintWriter out = httpServletResponse.getWriter();

            returnMap.put("status", SystemCode.NEED_LOGIN.getCode());
            returnMap.put("msg",SystemCode.NEED_LOGIN.getMsg());
            out.print(JsonUtil.obj2StrPretty(returnMap));

            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("AuthInterceptor.postHandle()方法");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("AuthInterceptor.afterCompletion()方法");
    }
}
