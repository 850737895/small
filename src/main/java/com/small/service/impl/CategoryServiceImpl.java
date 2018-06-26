package com.small.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.dao.CategoryMapper;
import com.small.pojo.Category;
import com.small.service.ICategoryService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 品类service的实现类
 * Created by 85073 on 2018/5/8.
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public SystemResponse<String> addCategory(Integer parentId,String categoryName) {
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);

        Integer insertCount = categoryMapper.insert(category);
        if(insertCount>0) {
            return SystemResponse.createSuccessByMsg(SystemConst.ADD_CATEGORY_SUCCESS);
        }
        return SystemResponse.createErrorByMsg(SystemConst.ADD_CATEGORY_FAIL);
    }

    @Override
    public SystemResponse<String> updateCategoryName(Integer categoryId, String categoryName) {

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int updateCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(updateCount >0) {
            return SystemResponse.createSuccessByMsg(SystemConst.MODIFY_CATORYNAME_SUCCESS);
        }
        return SystemResponse.createSuccessByMsg(SystemConst.MODIFY_CATORYNAME_FAIL);
    }

    @Override
    public SystemResponse<List<Category>> getCategoryByParentId(Integer categoryId) {
        List<Category> categoryList = Lists.newArrayList();
        categoryList = categoryMapper.getCategoryByParentId(categoryId);
        return SystemResponse.createSuccessByData(categoryList);
    }

    @Override
    public SystemResponse<List<Integer>> getCategoryDeepByParentId(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategoryById(categoryId,categorySet);
        List<Integer> categoryList = Lists.newArrayList();
        for (Category tempCategory:categorySet) {
            categoryList.add(tempCategory.getId());
        }
        return SystemResponse.createSuccessByData(categoryList);
    }

    private Set<Category> findChildCategoryById(Integer categoryId,Set<Category> categorySet) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!= null) {
            categorySet.add(category);
        }
        //递归算法,结束条件 categoryList 为空
        List<Category> categoryList = categoryMapper.getCategoryByParentId(categoryId);
        for (Category tempCategory: categoryList) {
            findChildCategoryById(tempCategory.getId(),categorySet);
        }
        return categorySet;
    }
}
