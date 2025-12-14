package com.kaola.file.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.file.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 文件上传接口
 *
 * @author Kaola Team
 */
@Tag(name = "文件上传接口", description = "图片、文件上传及删除接口")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Validated
public class UploadController {

    private final OssService ossService;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    @Operation(summary = "上传图片", description = "上传图片文件到OSS")
    @PostMapping("/image")
    public Result<String> uploadImage(
            @Parameter(description = "图片文件", required = true)
            @RequestParam @NotNull(message = "文件不能为空") MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }
        String url = ossService.uploadFile(file);
        return Result.success(url);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件URL
     */
    @Operation(summary = "上传文件", description = "上传通用文件到OSS")
    @PostMapping("/file")
    public Result<String> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam @NotNull(message = "文件不能为空") MultipartFile file) {
        String url = ossService.uploadFile(file);
        return Result.success(url);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 操作结果
     */
    @Operation(summary = "删除文件", description = "从OSS删除指定文件")
    @DeleteMapping("/delete")
    public Result<Boolean> deleteFile(
            @Parameter(description = "文件URL", required = true)
            @RequestParam @NotBlank(message = "文件URL不能为空") String fileUrl) {
        boolean success = ossService.deleteFile(fileUrl);
        return Result.success(success);
    }
}
