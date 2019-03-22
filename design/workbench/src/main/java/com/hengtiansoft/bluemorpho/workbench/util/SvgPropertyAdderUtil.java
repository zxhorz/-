package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.hengtiansoft.bluemorpho.workbench.domain.CtrlFlowSvgSize;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Aug 2, 2017 3:13:36 PM
 * 
 *          给svg图添加属性
 * 
 */
public class SvgPropertyAdderUtil {

	private static final Logger LOGGER = LogManager.getLogger(SvgPropertyAdderUtil.class);

	@SuppressWarnings("unchecked")
	public static CtrlFlowSvgSize addCssToControlFlowSvg(String svgPath, String newSvgPath) {
		CtrlFlowSvgSize ctrlFlowSvgSize = null;
		SAXReader reader = new SAXReader();
		File svgFile = new File(svgPath);
		Document document;
		try {
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			document = reader.read(svgFile);
			Element root = document.getRootElement();

			String width = root.attributeValue("width");
			String temp = width.substring(0, width.indexOf("px"));
			double pxWidth = Double.valueOf(temp).doubleValue();
			
			String height = root.attributeValue("height");
			String temp2 = height.substring(0, height.indexOf("px"));
			double pxHeight = Double.valueOf(temp2).doubleValue();
			
			ctrlFlowSvgSize = new CtrlFlowSvgSize(pxWidth, pxHeight);
			Element rootG = root.element("g");
			rootG.addAttribute("style", "stroke-width: 0.239976;");
			
			List<Element> gs = rootG.elements("g");
			for (Element g : gs) {
				Element path = g.element("path");
				if (path != null) {
					path.addAttribute("style", "fill: #ffffff; stroke: #000000;fill-opacity: 0;");
				}
				
				Element polygon = g.element("polygon");
				if (polygon != null) {
					polygon.addAttribute("style", "fill: #ffffff;stroke: #000000;fill-opacity: 1;");
				}
				
				String classAttr = g.attributeValue("class");
				if (classAttr != null && !classAttr.isEmpty() && (classAttr.contains("node ") || classAttr.contains(" node"))) {
					g.addAttribute("style", "fill: #ffffff;stroke: #000000;fill-opacity: 1;");
				}
				
				Element text = g.element("text");
				if (text != null) {
					text.addAttribute("style", "fill: #000000;text-anchor: middle;font-size: 6px;fill-opacity: 1;stroke-width: 0.05;");
				}
			}
			FileOutputStream fos = new FileOutputStream(newSvgPath);  
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");  
			OutputFormat of = new OutputFormat();
			XMLWriter writer = new XMLWriter(osw, of);  
			writer.write(document);  
			writer.close();  
			LOGGER.info("Finish adding css to svg!");
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return ctrlFlowSvgSize;
	}
	
}
