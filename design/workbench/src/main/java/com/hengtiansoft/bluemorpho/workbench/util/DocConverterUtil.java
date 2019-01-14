package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class DocConverterUtil {
    private static final Logger LOGGER = Logger.getLogger(DocConverterUtil.class);
    
	public static void convertHtmlToDoc(File htmlFile, String docPath) {
		try {
			byte[] b = FileUtils.readFileToByteArray(htmlFile);
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			POIFSFileSystem poifs = new POIFSFileSystem();
			DirectoryEntry directory = poifs.getRoot();
			DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
			FileOutputStream ostream = new FileOutputStream(docPath);
			poifs.writeFilesystem(ostream);
			bais.close();
			ostream.close();
		} catch (Exception e) {
		    LOGGER.error(e);
		}
	}

}
