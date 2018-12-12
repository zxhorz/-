package org.shaofan.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

public class Zip7Utils {
    public static void un7Zip(File srcFile, String path) {
        try {
            SevenZFile sevenZFile = new SevenZFile(srcFile);
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {

                // System.out.println(entry.getName());
                if (entry.isDirectory()) {
                    new File(path+ "/" + entry.getName()).mkdirs();
                    entry = sevenZFile.getNextEntry();
                    continue;
                }
                FileOutputStream out = new FileOutputStream(path
                        + File.separator + entry.getName());
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
                out.close();
                entry = sevenZFile.getNextEntry();
            }
            sevenZFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
