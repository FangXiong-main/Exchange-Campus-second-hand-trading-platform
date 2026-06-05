package com.exchange.controller;

import com.exchange.vo.Result;
import com.fangxiong.utils.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.exchange.constants.SystemConstants.*;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Resource
    private RedisUtils redisUtils;

    @Value("${exchange_file.local-path}")
    private String saveBasePath;

    @Value("${exchange_file.prefix}")
    private String urlPrefix;

    // 允许的图片类型
    private static final List<String> ALLOWED_IMG_TYPES = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    @PostMapping("/img")
    public Result uploadImg(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("图片不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return Result.error("文件格式不正确");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_IMG_TYPES.contains(suffix)) {
            return Result.error("仅支持上传 jpg、jpeg、png、gif、bmp、webp 格式图片");
        }

        String tempDirPath = saveBasePath + "temp/";
        File tempDir = new File(tempDirPath);

        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        String newFileName = redisUtils.uniqueIdGenerator(
                EXCHANGE_FILE_INCR_ID_KEY_PREFIX,
                EXCHANGE_UUID_TIME_KEY_FORMAT,
                EXCHANGE_ORDER_START_TIME,
                EXCHANGE_FILE_ID_TIMESTAMP_LENGTH,
                EXCHANGE_FILE_ID_MACHINE_CODE_LENGTH,
                EXCHANGE_FILE_ID_SEQUENCE_LENGTH,
                EXCHANGE_MACHINE_CODE
        ) + "." + suffix;

        // 保存到 temp 目录
        File targetFile = new File(tempDir, newFileName);
        file.transferTo(targetFile);

        // 返回可访问地址
        String url = urlPrefix + "temp/" + newFileName;

        return Result.success(url);
    }

}
