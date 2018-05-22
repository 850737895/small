package com.small.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 文件上传工具
 * Created by 85073 on 2018/5/10.
 */
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static final String ftpIp = PropertiesUtil.getPropertyValues("ftp.server.ip","47.104.128.12");

    private static final Integer ftpPort = 21;

    private static final String ftpUser = PropertiesUtil.getPropertyValues("ftp.user","ftpUser");

    private static final String ftpPass = PropertiesUtil.getPropertyValues("ftp.pass","Zw726515");

    private FTPClient ftpClient;

    public static boolean updateFile( List<File> fileList ) throws IOException {
        logger.info("开始上传文件");
        FTPUtil ftpUtil = new FTPUtil();
        boolean result = ftpUtil.uploadFile("images",fileList);
        logger.info("文件上传结束");
        return result;
    }

    /**
     * 上传文件
     * @param remotePath 上传的路径
     * @param fileList 文件列表
     */
    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        FileInputStream fis = null;
        boolean uploaded = true;
        //连接
        if(connectFtpServer()) {
            try {
                //切换工作路径
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem: fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常:{}",e.getMessage());
                uploaded = false;
                e.printStackTrace();
            }finally {
                if(fis!=null) {
                    fis.close();
                }
                if(ftpClient!=null) {
                    ftpClient.disconnect();
                }
            }

        }
        return uploaded;
    }

    public boolean connectFtpServer() {
        boolean connnectionFlag = true;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpIp,ftpPort);
            connnectionFlag = ftpClient.login(ftpUser,ftpPass);
        } catch (IOException e) {
            logger.error("连接ftpServer 异常:{}",e.getMessage());
            connnectionFlag = false;
        }
        return connnectionFlag;
    }
}
