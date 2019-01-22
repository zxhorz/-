package com.zxh.dormMG.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;


public class FilePathUtil {
	private static final Logger LOGGER = Logger.getLogger(FilePathUtil.class);
	// 以下内容使用方式后续更改，内容存入property文件，通过读入property文件一次性加载

	private static final String SYSTEMCONFIG = "systemconfig/";
	private static final String SMTP = "/smtp.properties";

    public static File getSmtpConfigPath() throws Exception{
        String filePath = SYSTEMCONFIG + SMTP;
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            throw new Exception("Cant't find smtp config");
        }
    }
    
    public static void download(File file,HttpServletResponse response) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
            return;
        }
        boolean isFolder = file.isDirectory();
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        if(!isFolder){
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getName(), "UTF-8")));
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                FileCopyUtils.copy(inputStream, response.getOutputStream());
                inputStream.close();
            }
        }else {
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getName()+".zip", "UTF-8")));
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
                CompressUtils.toZip(zos,"", file);
                response.flushBuffer();
                zos.close();
            }
        }
    }
}
