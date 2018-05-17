package com.small.service;

import com.github.pagehelper.PageInfo;
import com.small.common.SystemResponse;
import com.small.pojo.Shipping;

/**
 * 收货地址service 接口
 * Created by Administrator on 2018/5/17.
 */
public interface IShippingService {

    SystemResponse<Integer> add(Shipping shipping);

    SystemResponse<String> del(Integer userId,Integer shippingId);

    SystemResponse<String> update(Integer userId, Shipping shipping);

    SystemResponse<String> select(Integer userId, Integer shippingId);

    SystemResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize);
}
