package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SvgToPngUtil {
    private static final Logger LOGGER = LogManager.getLogger(SvgToPngUtil.class);
    
	public static void convertToPng(String svgCode, String pngFilePath) throws IOException, TranscoderException {
		File file = new File(pngFilePath);
		FileOutputStream outputStream = null;
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			convertToPng(svgCode, outputStream);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	private static void convertToPng(String svgCode, OutputStream outputStream)
			throws TranscoderException, IOException {
		try {
			byte[] bytes = svgCode.getBytes("utf-8");
			PNGTranscoder t = new PNGTranscoder();
			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
			TranscoderOutput output = new TranscoderOutput(outputStream);
			t.transcode(input, output);
			outputStream.flush();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
	}
	
}
