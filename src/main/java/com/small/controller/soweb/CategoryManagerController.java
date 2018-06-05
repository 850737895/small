package com.small.controller.soweb;

import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.Category;
import com.small.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 产品种类controller
 * Created by 85073 on 2018/5/8.
 */
@Controller
@RequestMapping("/manager/category")
@Slf4j
public class CategoryManagerController {

    @Autowired
    private ICategoryService categoryServiceImpl;
    /**
     * 增加产品种类
     * @param parentId  品类ID
     * @param categoryName 品类名称
     * @return SystemResponse
     */
    @RequestMapping(value = "/add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> addCategory(@RequestParam(value = "parentId",defaultValue = "0") Integer parentId,
                                      @RequestParam("categoryName")String categoryName) {

        //添加节点
        return categoryServiceImpl.addCategory(parentId,categoryName);
    }

    /**
     * 修改品类名称
     * @param categoryId 品类id
     * @param categoryName 品类名称
     * @return SystemResponse
     */
    @RequestMapping(value = "/set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> updateCategoryName(@RequestParam("categoryId") Integer categoryId,
                                                     @RequestParam("categoryName") String categoryName){
        if(StringUtils.isBlank(categoryName) || categoryId == null) {
            log.error("参数:categoryName为空:{}",categoryName);
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return categoryServiceImpl.updateCategoryName(categoryId,categoryName);
    }


    /**
     * 获取指定父节点 下的平行子节点
     * @param categoryId  categoryId
     * @param request  request
     * @return SystemResponse
     */
    @RequestMapping(value = "/get_category.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<List<Category>> getCategoryByParentId(Integer categoryId,HttpServletRequest request) {
        if( categoryId == null) {
            log.error("参数:categoryId:{}",categoryId);
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        return categoryServiceImpl.getCategoryByParentId(categoryId);
    }

    @RequestMapping(value = "/get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<List<Integer>> getCategoryDeepByParentId(@RequestParam("categoryId") Integer categoryId,
                                                                  HttpServletRequest request){
        return categoryServiceImpl.getCategoryDeepByParentId(categoryId);
    }

}
