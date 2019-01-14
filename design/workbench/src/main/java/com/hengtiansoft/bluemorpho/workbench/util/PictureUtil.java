package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class PictureUtil {
    private static final Logger LOGGER = Logger.getLogger(PictureUtil.class);

    public static String imageToBase64Str(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        // 加密
        return new String(Base64.encodeBase64(data));
    }
}
