package com.small.dao;

import com.small.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectByIdAndName(@Param("productId")Integer productId,@Param("productName")String productName);

    List<Product> selectByNameAndCategorys(@Param("keyWord")String keyWord,@Param("categoryList") List<Integer> categoryList);

    Integer selectStockByIdWithRowLock(Integer productId);
}