package com.small.service;

import com.small.common.SystemResponse;
import com.small.pojo.Category;

import java.util.List;

/**
 * 品类service
 * Created by 85073 on 2018/5/8.
 */
public interface ICategoryService {

    /**
     * 添加品类种类
     * @param parentId 父类id
     * @param categoryName 品类名称
     * @return SystemResponse
     */
    SystemResponse<String> addCategory(Integer parentId, String categoryName);

    /**
     * 修改品类名称
     * @param categoryId 品类ID
     * @param categoryName 评论名称
     * @return  SystemResponse
     */
    SystemResponse<String> updateCategoryName(Integer categoryId,String categoryName);

    /**
     * 获取平行节点
     * @param categoryId 种类id
     * @return SystemResponse
     */
    SystemResponse<List<Category>> getCategoryByParentId(Integer categoryId);

    /**
     * 递归查询子节点
     * @param categoryId  品类id
     * @return SystemResponse
     */
    SystemResponse<List<Integer>> getCategoryDeepByParentId(Integer categoryId);
}
