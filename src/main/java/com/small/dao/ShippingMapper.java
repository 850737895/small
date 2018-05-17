package com.small.dao;

import com.small.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int insertReturnId(Shipping shipping);

    int deleteByIdAndUserId(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);

    int updateByShipping(Shipping shipping);

    Shipping selectByIdAndUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> selectAllByUserId(Integer userId);
}