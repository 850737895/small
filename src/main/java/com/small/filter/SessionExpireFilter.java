package com.small.filter;

import com.small.utils.CookieUtil;
import com.small.common.SystemConst;
import com.small.utils.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 重置session 有效期的filter
 * Created by 85073 on 2018/6/3.
 */
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isNotBlank(tooken)) {
            String userStr = RedisPoolUtil.get(tooken);
            if(StringUtils.isNotBlank(userStr)) {
                RedisPoolUtil.setex(tooken, SystemConst.SessionCacheTime.SESSION_IN_REDIS_EXPIRE,userStr);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
