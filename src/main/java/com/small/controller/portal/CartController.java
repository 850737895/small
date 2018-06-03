package com.small.controller.portal;

import com.small.common.CookieUtil;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.ICartService;
import com.small.utils.JsonUtil;
import com.small.utils.RedisPoolUtil;
import com.small.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 购物车控制器
 * Created by 85073 on 2018/5/16.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartServiceImpl;

    /**
     * 购物车列表查询
     * @param request request
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse<CartVo> list(HttpServletRequest request) {
        //登录判断
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if (null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return cartServiceImpl.list(user.getId());
    }


    /**
     * 添加购物车
     * @param request request
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/add.do")
    @ResponseBody
    public SystemResponse<CartVo> add(HttpServletRequest request,Integer productId,Integer count) {
        //登录判断
        //登录判断
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return cartServiceImpl.add(user.getId(),productId,count);
    }

    /**
     * 更新产品数量
     * @param request request
     * @param productId productId
     * @param count count
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public SystemResponse<CartVo> update(HttpServletRequest request,Integer productId,Integer count) {
        //登录判断
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return cartServiceImpl.update(user.getId(),productId,count);
    }

    /**
     * 删除购物车中的产品数量
     * @param request request
     * @param productIds  产品id 集合 使用 ,分割
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/delete_product.do")
    @ResponseBody
    public SystemResponse<CartVo> del(HttpServletRequest request,String productIds) {
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(StringUtils.isBlank(productIds)) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return cartServiceImpl.del(user.getId(),productIds);
    }

    /**
     * 购物车勾选
     * @param request  request
     * @param productId productId
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/select.do")
    @ResponseBody
    public SystemResponse<CartVo> select(HttpServletRequest request,Integer productId) {
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(productId == null) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return cartServiceImpl.selectOrUnSelect(user.getId(),productId,SystemConst.CART_IS_CHECKED);
    }

    /**
     * 购物车中不够选
     * @param request   request
     * @param productId productId
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/un_select.do")
    @ResponseBody
    public SystemResponse<CartVo> unSelect(HttpServletRequest request,Integer productId) {
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(productId == null) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return cartServiceImpl.selectOrUnSelect(user.getId(),productId,SystemConst.CART_IS_UNCHECKED);
    }

    /**
     * 购物车全选
     * @param request request
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/select_all.do")
    @ResponseBody
    public SystemResponse<CartVo> selectAll(HttpServletRequest request) {
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }

        return cartServiceImpl.selectOrUnSelect(user.getId(),null,SystemConst.CART_IS_CHECKED);
    }

    /**
     * 购物车全不选
     * @param request request
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public SystemResponse<CartVo> select(HttpServletRequest request) {
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }

        return cartServiceImpl.selectOrUnSelect(user.getId(),null,SystemConst.CART_IS_UNCHECKED);
    }

    /**
     * 获取购物车中产品的数量
     * @param request request
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/get_cart_product_count.do")
    @ResponseBody
    public SystemResponse<Integer> getCartProductCount(HttpServletRequest request) {
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createSuccessByMsg("0");
        }
        return cartServiceImpl.getCartProductCount(user.getId());
    }


    public CartController() {
        System.out.println("-----------------------------------------------------------------");
    }


}
