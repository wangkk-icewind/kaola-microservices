package com.kaola.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS文件服务接口
 *
 * @author Kaola Team
 */
public interface OssService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件URL
     */
    String uploadFile(MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否成功
     */
    boolean deleteFile(String fileUrl);

    /**
     * 生成签名URL
     *
     * @param objectKey 对象键
     * @return 签名URL
     */
    String generatePresignedUrl(String objectKey);
}
