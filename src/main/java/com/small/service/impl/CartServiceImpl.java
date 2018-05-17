package com.small.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.dao.CartMapper;
import com.small.dao.ProductMapper;
import com.small.pojo.Cart;
import com.small.pojo.Product;
import com.small.service.ICartService;
import com.small.utils.BigDecimalUtils;
import com.small.utils.PropertiesUtil;
import com.small.vo.CartProductVo;
import com.small.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车service 实现类
 * Created by 85073 on 2018/5/16.
 */
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public SystemResponse<CartVo> list(Integer userId) {
        return SystemResponse.createSuccessByData(assembleCartVo(userId));
    }

    @Override
    public SystemResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if(productId == null || count == null){
            return SystemResponse.createErrorByCodeMsg(SystemCode.ILLEGAL_ARGUMENT.getCode(),SystemCode.ILLEGAL_ARGUMENT.getMsg());
        }
        //判断用户是否买过该产品
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart == null) {
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setQuantity(count);
            cartItem.setProductId(productId);
            cartItem.setChecked(SystemConst.CART_IS_CHECKED);
            cartMapper.insert(cartItem);
        }else {
            cart.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public SystemResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart !=null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return list(userId);
    }

    @Override
    public SystemResponse<CartVo> del(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        int delCount = cartMapper.deleteByProductIdAndUserId(userId,productIdList);
        return list(userId);
    }

    @Override
    public SystemResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer cartIsChecked) {
        cartMapper.selectOrUnSelect(userId,productId,cartIsChecked);
        return list(userId);
    }

    @Override
    public SystemResponse<Integer> getCartProductCount(Integer userId) {
        Integer cartProductCount = cartMapper.selectCartProductCountByUserId(userId);
        return null;
    }


    /**
     * 填充CartVo
     * @param userId 用户ID
     * @return CartVo
     */
    private CartVo assembleCartVo(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartsByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem: cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                //获取产品信息
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(null != product) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStock(product.getStock());

                    int buyLimitCount = 0;
                    //处理库存
                    if(product.getStock()<cartItem.getQuantity()) { //库存不足
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(SystemConst.LIMIT_NUM_FAIL);

                        Cart updateCart = new Cart();
                        updateCart.setProductId(product.getId());
                        updateCart.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(updateCart);

                    }else {
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(SystemConst.LIMIT_NUM_SUCCESS);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());

                    //计算总价
                    if(cartItem.getChecked() == SystemConst.CART_IS_CHECKED) {
                        cartTotalPrice = BigDecimalUtils.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                    }
                    cartProductVoList.add(cartProductVo);
                }

            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setImageHost(PropertiesUtil.getPropertyValues("ftp.server.http.prefix",SystemConst.DEFAULT_IMG_SERVER));
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(getAllCheckStatus(userId));
        return cartVo;
    }

    private boolean  getAllCheckStatus(Integer userId) {
        if(userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
