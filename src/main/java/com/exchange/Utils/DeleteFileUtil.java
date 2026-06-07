package com.exchange.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DeleteFileUtil {

    @Value("${exchange_file.delete-file-path-prefix}")
    private String deleteFilePathPrefix;

    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        filePath = deleteFilePathPrefix + filePath;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }
}
