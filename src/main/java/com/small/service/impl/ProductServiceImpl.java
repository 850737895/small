package com.small.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.small.common.ProductCode;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.dao.CategoryMapper;
import com.small.dao.ProductMapper;
import com.small.pojo.Category;
import com.small.pojo.Product;
import com.small.service.ICategoryService;
import com.small.service.IProductService;
import com.small.utils.DateUtil;
import com.small.utils.PropertiesUtil;
import com.small.vo.ProductDetailVo;
import com.small.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品服务实现类
 * Created by 85073 on 2018/5/8.
 */
@Service
public class ProductServiceImpl implements IProductService  {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService categoryServiceImpl;

    @Override
    public SystemResponse<String> saveOrUpdateProduct(Product product) {
        if(product == null) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        String[] subImgs = product.getSubImages().split(",");
        if(subImgs.length>0) {
            product.setMainImage(subImgs[0]);
        }
        //修改
        if(product.getId()!=null) {
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if(rowCount > 0) {
                return SystemResponse.createErrorByMsg(SystemConst.MODIFY_PRODUCT_FAIL);
            }
            return SystemResponse.createSuccessByMsg(SystemConst.MODIFY_PRODUCT_SUCCESS);
        }else {
            int rowCount = productMapper.insert(product);
            if(rowCount > 0) {
                return SystemResponse.createErrorByMsg(SystemConst.INSERT_PRODUCT_SUCCESS);
            }
            return SystemResponse.createSuccessByMsg(SystemConst.INSERT_PRODUCT_FAIL);
        }
    }

    @Override
    public SystemResponse<String> modifyProductStatus(Integer productId, Integer status) {
        if(productId == null) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0) {
            return SystemResponse.createSuccessByMsg(SystemConst.MODIFY_PRODUCT_STATUS_SUCCESS);
        }
        return SystemResponse.createSuccessByMsg(SystemConst.MODIFY_PRODUCT_STATUS_FAIL);
    }

    @Override
    public SystemResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.ILLEGAL_ARGUMENT.getCode(),SystemCode.ILLEGAL_ARGUMENT.getMsg());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(null == product) {
            return SystemResponse.createErrorByMsg(SystemConst.PRODUCT_NOT_EISTS_OR_DOWNLINE);
        }

        return SystemResponse.createSuccessByData(assembleProductDetailVo(product));
    }

    @Override
    public SystemResponse<PageInfo> selectList(Integer pageNum, Integer pageSize) {
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        //查询逻辑
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:productList) {
            productListVoList.add(assebleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return SystemResponse.createSuccessByData(pageInfo);
    }

    @Override
    public SystemResponse<PageInfo> productSearch(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)) {
            productName = new StringBuffer("").append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByIdAndName(productId,productName);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:productList) {
            productListVoList.add(assebleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return SystemResponse.createSuccessByData(pageInfo);
    }

    @Override
    public SystemResponse<ProductDetailVo> getProductDetail4Portal(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return SystemResponse.createErrorByMsg("查询的产品不存在");
        }
        if(product.getStatus()!= ProductCode.ON_SALE.getCode()) {
            return SystemResponse.createErrorByMsg("产品已下架");
        }
        return SystemResponse.createSuccessByData(assembleProductDetailVo(product));
    }

    @Override
    public SystemResponse<PageInfo> getProductKeywordAndCategoryId(String keyWord, Integer categoryId, Integer pageNum, Integer pageSize,String orderBy) {
        List<Integer> categoryList = Lists.newArrayList();
        if(categoryId!=null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null & StringUtils.isBlank(keyWord)) {
                PageHelper.startPage(pageNum,pageSize);
                //返回一个空集合
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return SystemResponse.createSuccessByData(pageInfo);
            }
            categoryList = categoryServiceImpl.getCategoryDeepByParentId(category.getId()).getData();
        }
        PageHelper.startPage(pageNum,pageSize);
        if(!StringUtils.isBlank(keyWord)) {
            keyWord = new StringBuffer().append("%").append(keyWord).append("%").toString();
        }
        if(StringUtils.isNotBlank(orderBy)) {
            String[] orderByArray = orderBy.split("_");
            String orderByKey =orderByArray[0];
            String orderBySort = orderByArray[1];
            PageHelper.orderBy(orderByKey+"_"+orderBySort);
        }
        List<Product> productList = productMapper.selectByNameAndCategorys(keyWord,categoryList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem: productList) {
            productListVoList.add(assebleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return SystemResponse.createSuccessByData(pageInfo);
    }

    /**
     * 填充productDetailVo
     * @param product 产品对象
     * @return ProductDetailVo
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null) {//默认根节点
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setImageHost(PropertiesUtil.getPropertyValues("ftp.server.http.prefix","http://img.small.com//images/"));

        productDetailVo.setUpdateTime(DateUtil.date2DateStr(product.getUpdateTime()));
        productDetailVo.setCreateTime(DateUtil.date2DateStr(product.getCreateTime()));
        return productDetailVo;

    }

    /**
     * 填充productListVo
     * @param product 产品对象
     * @return ProductListVo
     */
    private ProductListVo assebleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        BeanUtils.copyProperties(product,productListVo);
        productListVo.setImageHost(PropertiesUtil.getPropertyValues("ftp.server.http.prefix","http://img.small.com//images/"));
        return productListVo;
    }
}
