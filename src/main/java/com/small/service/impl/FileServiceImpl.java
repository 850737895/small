package com.small.service.impl;

import com.google.common.collect.Lists;
import com.small.service.IFileService;
import com.small.utils.FTPUtil;
import com.small.utils.PropertiesUtil;
import com.small.vo.FileVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传对象
 * Created by 85073 on 2018/5/10.
 */
@Service
public class FileServiceImpl implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public FileVo upload(MultipartFile file, String path) {
        FileVo fileVo = new FileVo();
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);
        String targetFileName = UUID.randomUUID().toString()+"."+fileSuffix;
        logger.info("文件上传名:{}文件上传路径:{} 新文件名:{}",fileName,path,targetFileName);
        //创建文件所在的路径
        File targetDir = new File(path);
        if(!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File targetFile = new File(path,targetFileName);
        try {
            //文件上传到应用服务器
            file.transferTo(targetFile);

            //上传到ftp服务器
            FTPUtil.updateFile(Lists.newArrayList(targetFile));
            //删除应用服务器的图片
            targetFile.delete();
            fileVo.setFileName(targetFileName);
            fileVo.setUrl(PropertiesUtil.getPropertyValues("ftp.server.http.prefix","http://img.small.com//images/")+targetFileName);
        } catch (IOException e) {
            logger.error("上传图片异常{}",e.getMessage());
            return null;
        }
        return fileVo;
    }
}
