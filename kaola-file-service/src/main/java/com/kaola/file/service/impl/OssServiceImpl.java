package com.kaola.file.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.kaola.file.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * OSS文件服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {

    @Value("${aliyun.oss.endpoint:oss-cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret:}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name:kaola}")
    private String bucketName;

    @Value("${aliyun.oss.url-prefix:https://kaola.oss-cn-hangzhou.aliyuncs.com}")
    private String urlPrefix;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        if (accessKeyId != null && !accessKeyId.isEmpty() &&
            accessKeySecret != null && !accessKeySecret.isEmpty()) {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            log.info("OSS客户端初始化成功");
        } else {
            log.warn("OSS配置不完整，文件上传功能不可用");
        }
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("上传文件, fileName: {}, size: {}", file.getOriginalFilename(), file.getSize());

        if (ossClient == null) {
            throw new RuntimeException("OSS客户端未初始化");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 生成文件路径
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String objectKey = "upload/" + datePath + "/" + fileName;

        try {
            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 上传文件
            ossClient.putObject(bucketName, objectKey, file.getInputStream(), metadata);

            String fileUrl = urlPrefix + "/" + objectKey;
            log.info("文件上传成功, url: {}", fileUrl);

            return fileUrl;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        log.info("删除文件, fileUrl: {}", fileUrl);

        if (ossClient == null) {
            throw new RuntimeException("OSS客户端未初始化");
        }

        try {
            // 从URL中提取objectKey
            String objectKey = fileUrl.replace(urlPrefix + "/", "");

            // 删除文件
            ossClient.deleteObject(bucketName, objectKey);

            log.info("文件删除成功, objectKey: {}", objectKey);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(String objectKey) {
        log.info("生成签名URL, objectKey: {}", objectKey);

        if (ossClient == null) {
            throw new RuntimeException("OSS客户端未初始化");
        }

        try {
            // 设置URL过期时间为1小时
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);

            // 生成签名URL
            URL url = ossClient.generatePresignedUrl(bucketName, objectKey, expiration);

            return url.toString();
        } catch (Exception e) {
            log.error("生成签名URL失败", e);
            throw new RuntimeException("生成签名URL失败: " + e.getMessage());
        }
    }
}
