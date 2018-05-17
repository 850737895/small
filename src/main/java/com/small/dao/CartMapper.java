package com.small.dao;

import com.small.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    /**
     * 通过用户ID  查询购物车信息
     * @param userId 用户ID
     * @return List<Cart>
     */
    List<Cart> selectCartsByUserId(Integer userId);

    /**
     * 判断购物车是否全选
     * @param userId 用户ID
     * @return >0  没有全选
     */
    int selectCartProductCheckedStatusByUserId(Integer userId);

    /**
     * 判断用户是否购买过该商品
     * @param userId 用户ID
     * @param productId 产品ID
     * @return Cart
     */
    Cart selectByUserIdAndProductId(@Param("userId")Integer userId,@Param("productId") Integer productId);

    /**
     * 批量删除购物车中的产品
     * @param userId 用户ID
     * @param productIdList 产品列表
     * @return deleteByProductIdAndUserId
     */
    int deleteByProductIdAndUserId(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    /**
     * 购物车中产品选中或者不选
     * @param userId 用户ID
     * @param productId 产品Id
     * @param cartIsChecked 选择状态
     */
    void selectOrUnSelect(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checkFlag") Integer cartIsChecked);

    int selectCartProductCountByUserId(Integer userId);
}