package com.small.service;

import com.github.pagehelper.PageInfo;
import com.small.common.SystemResponse;
import com.small.pojo.Product;
import com.small.vo.ProductDetailVo;
import com.small.vo.ProductListVo;

/**
 * 产品接口
 * Created by 85073 on 2018/5/8.
 */
public interface IProductService {

    /**
     * 更新或者是新增产品
     * @param product 产品对象
     * @return SystemResponse
     */
    SystemResponse<String> saveOrUpdateProduct(Product product);

    /**
     * 修改产品上下架状态
     * @param productId 产品ID
     * @param status  产品上下架状态
     * @return modifyProductStatus
     */
    SystemResponse<String> modifyProductStatus(Integer productId, Integer status);

    /**
     * 获取产品详情
     * @param productId 产品id
     * @return SystemResponse
     */
    SystemResponse<ProductDetailVo> getProductDetail(Integer productId);

    /**
     * 分页查询产品
     * @param pageNum 页码
     * @param pageSize 每页显示多少条
     * @return SystemResponse<PageInfo>
     */
    SystemResponse<PageInfo> selectList(Integer pageNum, Integer pageSize);

    /**
     * 根据条件查询产品
     * @param productName 产品名称
     * @param productId 产品ID
     * @param pageNum 页码
     * @param pageSize 每页多少条
     * @return SystemResponse
     */
    SystemResponse<PageInfo> productSearch(String productName, Integer productId, Integer pageNum, Integer pageSize);

    /**
     * 提供给前端用的获取产品详情接口
     * @param productId 产品Id
     * @return SystemResponse<ProductDetailVo>
     */
    SystemResponse<ProductDetailVo> getProductDetail4Portal(Integer productId);

    /**
     * 提供给前端查询产品列表
     * @param keyWord productName
     * @param categoryId categoryId
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @return SystemResponse<PageInfo>
     */
    SystemResponse<PageInfo> getProductKeywordAndCategoryId(String keyWord, Integer categoryId, Integer pageNum, Integer pageSize,String orderBy);
}
