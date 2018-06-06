package com.small.Resolver;

import com.small.pojo.User;
import com.small.utils.CookieUtil;
import com.small.utils.JsonUtil;
import com.small.utils.RedisShardingPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义参数解析器
 * Created by 85073 on 2018/6/5.
 */
public class UserArgsResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request =nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String token = CookieUtil.readCookie(request);
        String userStr = RedisShardingPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userStr,User.class);
        return user;
    }
}
