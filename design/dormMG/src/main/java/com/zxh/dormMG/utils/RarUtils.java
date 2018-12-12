package com.zxh.dormMG.utils;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.exception.RarException.RarExceptionType;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * RarUtils on spring-boot-filemanager
 *
 * @author <a href="mailto:akhuting@hotmail.com">Alex Yang</a>
 * @date 2016年08月25日 10:13
 */
public class RarUtils {

    public static void unRarFile(File srcRar, String dstDirectoryPath) throws IOException, RarException {

        Archive archive = new Archive(srcRar);
        FileHeader fh = archive.nextFileHeader();
        while (fh != null) {
            String path = fh.getFileNameString().replaceAll("\\\\", "/");
            File dirFile = new File(dstDirectoryPath + File.separator + path);
            if (fh.isDirectory()) { // 文件夹
                dirFile.mkdirs();
            } else { // 文件
                try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压.
                    if (!dirFile.exists()) {
                        if (!dirFile.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
                            dirFile.getParentFile().mkdirs();
                        }
                        dirFile.createNewFile();
                    }
                    FileOutputStream os = new FileOutputStream(dirFile);
                    archive.extractFile(fh, os);
                    os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            fh = archive.nextFileHeader();
        }
        archive.close();

    }

}
