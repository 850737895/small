package com.small.controller.portal;

import com.small.utils.CookieUtil;
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
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse<CartVo> list(User user) {
        return cartServiceImpl.list(user.getId());
    }


    /**
     * 添加购物车
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/add.do")
    @ResponseBody
    public SystemResponse<CartVo> add(User user,Integer productId,Integer count) {
        return cartServiceImpl.add(user.getId(),productId,count);
    }

    /**
     * 更新产品数量
     * @param productId productId
     * @param count count
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public SystemResponse<CartVo> update(User user,Integer productId,Integer count) {
        return cartServiceImpl.update(user.getId(),productId,count);
    }

    /**
     * 删除购物车中的产品数量
     * @param productIds  产品id 集合 使用 ,分割
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/delete_product.do")
    @ResponseBody
    public SystemResponse<CartVo> del(User user,String productIds) {

        if(StringUtils.isBlank(productIds)) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return cartServiceImpl.del(user.getId(),productIds);
    }

    /**
     * 购物车勾选
     * User user
     * @param productId productId
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/select.do")
    @ResponseBody
    public SystemResponse<CartVo> select(User user,Integer productId) {

        if(productId == null) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return cartServiceImpl.selectOrUnSelect(user.getId(),productId,SystemConst.CART_IS_CHECKED);
    }

    /**
     * 购物车中不够选
     *  User user
     * @param productId productId
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/un_select.do")
    @ResponseBody
    public SystemResponse<CartVo> unSelect(User user,Integer productId) {
        if(productId == null) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return cartServiceImpl.selectOrUnSelect(user.getId(),productId,SystemConst.CART_IS_UNCHECKED);
    }

    /**
     * 购物车全选
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/select_all.do")
    @ResponseBody
    public SystemResponse<CartVo> selectAll(User user) {
        return cartServiceImpl.selectOrUnSelect(user.getId(),null,SystemConst.CART_IS_CHECKED);
    }

    /**
     * 购物车全不选
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public SystemResponse<CartVo> select(User user) {
        return cartServiceImpl.selectOrUnSelect(user.getId(),null,SystemConst.CART_IS_UNCHECKED);
    }

    /**
     * 获取购物车中产品的数量
     * @return SystemResponse<CartVo>
     */
    @RequestMapping("/get_cart_product_count.do")
    @ResponseBody
    public SystemResponse<Integer> getCartProductCount(User user) {
        return cartServiceImpl.getCartProductCount(user.getId());
    }


    public CartController() {
        System.out.println("-----------------------------------------------------------------");
    }


}
