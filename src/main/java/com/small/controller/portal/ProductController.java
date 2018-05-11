package com.small.controller.portal;

import com.github.pagehelper.PageInfo;
import com.small.common.SystemCode;
import com.small.common.SystemResponse;
import com.small.service.IProductService;
import com.small.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 前端产品控制类
 * Created by 85073 on 2018/5/11.
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    private IProductService productServiceImpl;

    @RequestMapping("/detail.do")
    @ResponseBody
    public SystemResponse<ProductDetailVo> detail(Integer productId) {
        if(productId == null) {
            return SystemResponse.createErrorByMsg("查询参数错误productId");
        }
        return productServiceImpl.getProductDetail4Portal(productId);
    }

    /**
     * 查询产品列表
     * @param keyword 关键字  productName
     * @param categoryId 品类id
     * @param pageNum 查询第几页
     * @param pageSize 页码
     * @param orderBy 价格排序  price_desc  pricue_asc
     * @return SystemResponse<ProductListVo>
     */
    public SystemResponse<PageInfo> list(@RequestParam(value = "keyWord",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue ="10")Integer pageSize,
                                         @RequestParam(value="orderBy")String orderBy) {
        if(StringUtils.isBlank(keyword) && categoryId == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.ILLEGAL_ARGUMENT.getCode(),SystemCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return productServiceImpl.getProductKeywordAndCategoryId(keyword,categoryId,pageNum,pageSize,orderBy);
    }

}
