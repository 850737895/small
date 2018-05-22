package com.small.service;

import com.small.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传service
 * Created by 85073 on 2018/5/10.
 */
public interface IFileService {

    /**
     * 文件上传
     * @param file 文件对象
     * @param path 上传路径
     * @return FileVo
     */
    FileVo upload(MultipartFile file,String path);
}
