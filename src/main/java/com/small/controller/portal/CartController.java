package com.small.controller.portal;

import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.ICartService;
import com.small.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * @param session session
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse<CartVo> list(HttpSession session) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return cartServiceImpl.list(user.getId());
    }


    /**
     * 添加购物车
     * @param session session
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/add.do")
    @ResponseBody
    public SystemResponse<CartVo> add(HttpSession session,Integer productId,Integer count) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return cartServiceImpl.add(user.getId(),productId,count);
    }

    /**
     * 更新产品数量
     * @param session session
     * @param productId productId
     * @param count count
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public SystemResponse<CartVo> update(HttpSession session,Integer productId,Integer count) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return cartServiceImpl.update(user.getId(),productId,count);
    }

    /**
     * 删除购物车中的产品数量
     * @param session session
     * @param productIds  产品id 集合 使用 ,分割
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/delete_product.do")
    @ResponseBody
    public SystemResponse<CartVo> del(HttpSession session,String productIds) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
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
     * @param session  session
     * @param productId productId
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/select.do")
    @ResponseBody
    public SystemResponse<CartVo> select(HttpSession session,Integer productId) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
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
     * @param session   session
     * @param productId productId
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/un_select.do")
    @ResponseBody
    public SystemResponse<CartVo> unSelect(HttpSession session,Integer productId) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
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
     * @param session session
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/select_all.do")
    @ResponseBody
    public SystemResponse<CartVo> selectAll(HttpSession session) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }

        return cartServiceImpl.selectOrUnSelect(user.getId(),null,SystemConst.CART_IS_CHECKED);
    }

    /**
     * 购物车全不选
     * @param session session
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public SystemResponse<CartVo> select(HttpSession session) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }

        return cartServiceImpl.selectOrUnSelect(user.getId(),null,SystemConst.CART_IS_UNCHECKED);
    }

    /**
     * 获取购物车中产品的数量
     * @param session session
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/get_cart_product_count.do")
    @ResponseBody
    public SystemResponse<Integer> getCartProductCount(HttpSession session) {
        //登录判断
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(null == user) {
            return SystemResponse.createSuccessByMsg("0");
        }
        return cartServiceImpl.getCartProductCount(user.getId());
    }



}
