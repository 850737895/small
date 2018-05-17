package com.small.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.small.common.SystemResponse;
import com.small.dao.ShippingMapper;
import com.small.pojo.Shipping;
import com.small.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * service 接口实现类
 * Created by Administrator on 2018/5/17.
 */
@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public SystemResponse<Integer> add(Shipping shipping) {
        if(shipping == null) {
            return SystemResponse.createErrorByMsg("新增收货地址失败");
        }
        shippingMapper.insertReturnId(shipping);
        return SystemResponse.createSuccessByData(shipping.getId());
    }

    @Override
    public SystemResponse<String> del(Integer userId,Integer shippingId) {
        int delCount = shippingMapper.deleteByIdAndUserId(userId,shippingId);
        if(delCount>0) {
            return SystemResponse.createSuccessByMsg("删除地址成功");
        }
        return SystemResponse.createErrorByMsg("删除地址失败");
    }

    @Override
    public SystemResponse<String> update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int updateCount = shippingMapper.updateByShipping(shipping);
        if(updateCount>0) {
            return SystemResponse.createSuccessByMsg("修改地址成功");
        }
        return SystemResponse.createErrorByMsg("修改地址失败");
    }

    @Override
    public SystemResponse<String> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByIdAndUserId(userId,shippingId);
        if(shipping == null) {
            return SystemResponse.createErrorByMsg("没有查询到改收货地址记录");
        }
        return SystemResponse.createSuccessByData(shipping);
    }

    @Override
    public SystemResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectAllByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return SystemResponse.createSuccessByData(pageInfo);
    }
}
