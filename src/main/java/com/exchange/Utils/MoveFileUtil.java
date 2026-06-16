package com.exchange.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class MoveFileUtil
{
    @Value("${exchange_file.local-path}")
    private String saveBasePath;

    @Value("${exchange_file.prefix}")
    private String urlPrefix;
    public String moveTempToReal(String tempUrl) {
        try {
            // 1. 截取掉前缀，只留 temp/123.png
            String tempPath = tempUrl.replace(urlPrefix, "");

            // 2. 源文件：temp 目录
            File srcFile = new File(saveBasePath + "/" + tempPath);

            // 3. 目标文件名
            String fileName = tempPath.replace("temp/", "");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateDir = sdf.format(new Date()) + "/";
            File realDir = new File(saveBasePath + "/" + dateDir);
            if (!realDir.exists()) {
                realDir.mkdirs();
            }

            File destFile = new File(realDir, fileName);

            boolean success = srcFile.renameTo(destFile);

            if (success) {
                return urlPrefix + dateDir + fileName;
            }
        } catch (Exception e) {
            log.info("移动文件失败：{}", e.getMessage());
        }
        return null;
    }

    public String moveRealToTemp(String realUrl) {
        try {
            String realPath = realUrl.replace(urlPrefix, "");
            File srcFile = new File(saveBasePath+realPath);
            String fileName = srcFile.getName();
            File tempFile = new File(saveBasePath+"temp/", fileName);
            srcFile.renameTo(tempFile);
            return urlPrefix + "temp/" + fileName;
        } catch (Exception e) {
            log.info("移动到Temp文件失败：{}", e.getMessage());
        }
        return null;
    }
}
