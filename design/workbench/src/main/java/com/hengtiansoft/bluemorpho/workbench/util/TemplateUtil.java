package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date May 18, 2018
 */
public class TemplateUtil {
	public static final String FSEP = System.getProperty("file.separator", "/");
	private static final Logger LOGGER = Logger.getLogger(TemplateUtil.class);

	public static void generateFile(String templateName, String targetPath,
			Map<String, Object> data) {
		try {
			// 创建配置实例
			Configuration configuration = new Configuration();
			// 设置编码
			configuration.setDefaultEncoding("UTF-8");
			// ftl模板文件
			configuration.setClassForTemplateLoading(TemplateUtil.class,
					"/template");
			// 获取模板
			Template template = configuration.getTemplate(templateName);
			File targetFile = new File(targetPath);
			if (targetFile.exists()) {
				targetFile.delete();
			}
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(targetFile), "UTF-8"));
			template.process(data, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			LOGGER.error("TempletUtil", e);
		} catch (TemplateException e) {
			LOGGER.error("TempletUtil", e);
		}
	}
}
