package com.small.dao;

import com.small.pojo.Order;
import com.small.vo.OrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId")Integer userId,@Param("orderNo")Long orderId);

    Order selectByOrderNo(String outOrderNo);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAll();
}