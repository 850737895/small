package com.small.service;

import com.small.common.SystemResponse;
import com.small.vo.CartVo;

/**
 * 购物车接口
 * Created by 85073 on 2018/5/16.
 */
public interface ICartService {
    /**
     * 查询购物车列表
     * @param userId  用户ID
     * @return  购物车列表
     */
    SystemResponse<CartVo> list(Integer userId);

    /**
     * 添加购物车
     * @param userId 用户ID
     * @param productId 产品ID
     * @param count 购买数量
     * @return SystemResponse<CartVo>
     */
    SystemResponse<CartVo> add(Integer userId,Integer productId,Integer count);

    /**
     * 更新购物车产品的数据
     * @param userId 用户I的
     * @param productId  产品id
     * @param count  产品数量
     * @return SystemResponse<CartVo>
     */
    SystemResponse<CartVo> update(Integer userId,Integer productId,Integer count);

    /**
     * 删除产品数据
     * @param userId 用户Id
     * @param productIds 产品列表
     * @return SystemResponse<CartVo>
     */
    SystemResponse<CartVo> del(Integer userId, String productIds);

    /**
     *选中/不选
     * @param userId 用户ID
     * @param productId 产品ID
     * @param cartIsChecked 选择状态
     * @return  SystemResponse<CartVo>
     */
    SystemResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer cartIsChecked);

    /**
     *获取购物车数量
     * @param userId userId
     * @return SystemResponse<CartVo>
     */
    SystemResponse<Integer> getCartProductCount(Integer userId);
}
