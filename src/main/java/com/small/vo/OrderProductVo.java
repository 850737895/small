package com.small.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车产品VO
 * Created by 85073 on 2018/5/22.
 */
public class OrderProductVo {

    private BigDecimal productTotalPrice;

    private String imageHost;

    private List<OrderItemVo> orderItemVoList;

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }
}
