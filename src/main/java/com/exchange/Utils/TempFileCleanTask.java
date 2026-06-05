package com.exchange.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
@Slf4j
public class TempFileCleanTask {
    @Value("${exchange_file.local-path}")
    private String saveBasePath;

    @Scheduled(cron = "0 0 1 * * ?")
    public void clearTempImages() {
        // 临时目录路径
        String tempPath = saveBasePath + "temp/";
        File tempDir = new File(tempPath);

        if (!tempDir.exists()) return;

        File[] files = tempDir.listFiles();
        if (files == null) return;

        long now = System.currentTimeMillis();
        // 过期时间：12小时前的文件都删掉
        long expireTime = 12 * 60 * 60 * 1000;

        for (File file : files) {
            // 文件创建时间超过12小时 → 删除
            if (now - file.lastModified() > expireTime) {
                file.delete();
            }
        }
        log.info("定时清理完成：删除过期临时图片");
    }
}
