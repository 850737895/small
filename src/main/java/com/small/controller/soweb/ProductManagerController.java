package com.small.controller.soweb;

import com.small.common.SystemResponse;
import com.small.pojo.Product;
import com.small.service.IFileService;
import com.small.service.IProductService;
import com.small.service.IUserService;
import com.small.vo.FileVo;
import com.small.vo.ProductDetailVo;
import com.small.vo.RichTextVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 产品管理
 * Created by 85073 on 2018/5/8.
 */
@Controller
@RequestMapping("/manager/product")
public class ProductManagerController {

    @Autowired
    private IProductService productServiceImpl;
    @Autowired
    private IFileService fileServiceImpl;

    /**
     * 新增或修改产品信息
     * @param product product
     * @return saveOrUpdateProduct
     */
    @RequestMapping(value = "/save.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> saveOrUpdateProduct(Product product) {
        return productServiceImpl.saveOrUpdateProduct(product);
    }

    /**
     * 产品上下架功能
     * @param status status
     * @param productId productId
     * @return SystemResponse
     */
    @RequestMapping(value = "/set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> modifyProductStatus( @RequestParam(value = "status",defaultValue = "1")
                                                    Integer status, Integer productId) {
        return productServiceImpl.modifyProductStatus(productId,status);
    }

    /**
     * 获取产品详情
     * @param productId 产品
     * @return SystemResponse<ProductDetailVo>
     */
    @RequestMapping(value = "/detail.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<ProductDetailVo> productDetail( Integer productId ) {
        return productServiceImpl.getProductDetail(productId);
    }

    /**
     * 查询列表
     * @param pageNum 查询页
     * @param pageSize 每页大小
     * @return productList
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse productList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        return productServiceImpl.selectList(pageNum,pageSize);
    }

    /**
     * 产品搜索功能
     * @param productName 产品名称
     * @param productId 产品id
     * @param pageNum 页码
     * @param pageSize 每页多少数据
     * @return productSearch
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public SystemResponse productSearch(String productName,Integer productId,
                                      @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        return productServiceImpl.productSearch(productName,productId,pageNum,pageSize);
    }

    /**
     * 普通文件上传
     * @param request request
     * @param file file
     * @return SystemResponse<FileVo>
     */
    @RequestMapping("/upload.do")
    @ResponseBody
    public SystemResponse<FileVo> fileUpload(HttpServletRequest request, @RequestParam(value = "upload_file",required = false) MultipartFile file) {
        if(file.getSize() == 0) {
            return SystemResponse.createErrorByMsg("上传文件为空");
        }
        String path = request.getServletContext().getRealPath("upload");
        FileVo fileVo =fileServiceImpl.upload(file,path);
        if(fileVo == null) {
            SystemResponse.createErrorByMsg("文件上传异常");
        }
        return SystemResponse.createSuccessByData(fileVo);
    }

    /**
     * 富文本文件上传
     * @param request request
     * @param file file
     * @param response response
     * @return RichTextVo
     */
    @RequestMapping("/richtext_img_upload.do")
    @ResponseBody
    public RichTextVo richtextImgUpload(HttpServletRequest request,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletResponse response ) {
        RichTextVo richTextVo = new RichTextVo();
        richTextVo.setSuccess(false);
        String path = request.getServletContext().getRealPath("upload");
        FileVo fileVo =fileServiceImpl.upload(file,path);
        if(fileVo == null) {
            richTextVo.setMsg("文件上传异常");
            return richTextVo;
        }
        richTextVo.setSuccess(true);
        richTextVo.setMsg("文件上传成功");
        richTextVo.setFile_path(fileVo.getUrl());
        response.setHeader("Access-Control-Allow-Headers","X-File-Name");
        return richTextVo;
    }
}
