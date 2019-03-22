package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.log4j.Logger;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.exception.RarException.RarExceptionType;
import com.github.junrar.rarfile.FileHeader;
import com.hengtiansoft.bluemorpho.workbench.services.SourceCodeService;

public class CompressUtils {
	private static final Logger LOGGER = Logger.getLogger(SourceCodeService.class);
	
    public static void toZip(ZipOutputStream out, String path, File... srcFiles)
            throws IOException {
        path = path.replaceAll("\\*", "/");
        if (!path.endsWith("/") && !path.equals("")) {
            path += "/";
        }
        byte[] buf = new byte[1024];
        for (File srcFile : srcFiles) {
            if (srcFile.isDirectory()) {
                File[] files = srcFile.listFiles();
                String srcPath = srcFile.getName();
                srcPath = srcPath.replaceAll("\\*", "/");
                if (!srcPath.endsWith("/")) {
                    srcPath += "/";
                }
                out.putNextEntry(new ZipEntry(path + srcPath));
                toZip(out, path + srcPath, files);
            } else {
                try (FileInputStream in = new FileInputStream(srcFile)) {
                    out.putNextEntry(new ZipEntry(path + srcFile.getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                }
            }
        }
    }

    public static void unZip(File srcFile, String path){
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()) {
                    String dirPath = path + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    File targetFile = new File(path + "/" + entry.getName());
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();

                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[2048];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                	LOGGER.error("unZip file IO Exception");
                }
            }
        }
    }
    


    public static void unRar(File srcFile, String path) {
        String rarFileName = srcFile.getName();
        Archive archive = null;
        try {
            archive = new Archive(new FileInputStream(srcFile));
            if (archive.isEncrypted()) {
                throw new Exception(rarFileName + " IS ENCRYPTED!");
            }
            List<FileHeader> files = archive.getFileHeaders();
            for (FileHeader fh : files) {
                if (fh.isEncrypted()) {
                    throw new Exception(rarFileName + " IS ENCRYPTED!");
                }
                String fileName = fh.getFileNameW();
                if (fileName != null && fileName.trim().length() > 0) {
                    String saveFileName = path + "\\" + fileName;
                    File saveFile = new File(saveFileName);
                    File parent = saveFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    if (!saveFile.exists()) {
                        saveFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    try {
                        archive.extractFile(fh, fos);
                        fos.flush();
                        fos.close();
                    } catch (RarException e) {
                        if (e.getType().equals(RarExceptionType.notImplementedYet)) {
                        }
                    } finally {
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }finally{
            try {
                archive.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOGGER.error(e);
            }
        }
    }
    
    public static void unTarFile(File srcFile, String destPath)
            throws Exception {

        try (TarArchiveInputStream tais = new TarArchiveInputStream(
                new FileInputStream(srcFile))) {
            dearchive(new File(destPath), tais);
        }
    }


    private static void dearchive(File destFile, TarArchiveInputStream tais)
            throws Exception {

        TarArchiveEntry entry;
        while ((entry = tais.getNextTarEntry()) != null) {

            String dir = destFile.getPath() + File.separator + entry.getName();
            File dirFile = new File(dir);

            if (entry.isDirectory()) {
                dirFile.mkdirs();
            } else {
                dearchiveFile(dirFile, tais);
            }

        }
    }

    private static void dearchiveFile(File destFile, TarArchiveInputStream tais)
            throws Exception {

        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile));

        int count;
        byte data[] = new byte[1024];
        while ((count = tais.read(data, 0, 1024)) != -1) {
            bos.write(data, 0, count);
        }

        bos.close();
    }
    
    public static void unTargz(File gzFile, String descDir) throws Exception {

        GZIPInputStream inputStream = new GZIPInputStream((new FileInputStream(gzFile)));
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(inputStream);
        dearchive(new File(descDir), tarArchiveInputStream);

    }
    
    public static void un7Zip(File srcFile, String path){
        SevenZFile sevenZFile = null;
        try {sevenZFile = new SevenZFile(srcFile);
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {
                if (entry.isDirectory()) {
                    String dirPath = path + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                }else {
                    File targetFile = new File(path + "/" + entry.getName());
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                
                    FileOutputStream out = new FileOutputStream(targetFile);
                    byte[] content = new byte[(int) entry.getSize()];
                    sevenZFile.read(content, 0, content.length);
                    out.write(content);
                    out.close();
                }
                entry = sevenZFile.getNextEntry();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
        } catch (IOException e) {
            LOGGER.error(e);
        }finally{
            try {
                sevenZFile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOGGER.error(e);
            }
        }
    }
}
