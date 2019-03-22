package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.dto.MenuResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.SubMenu;
import com.hengtiansoft.bluemorpho.workbench.dto.SummaryTabMap;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;

@Service
public class SystemConfigService {
	private static final Logger LOGGER = Logger
			.getLogger(SystemConfigService.class);
	
	public List<MenuResponse> getMenuList() throws Exception {
		File file = FilePathUtil.getMenuConfigPath();
		if(null==file){
			return new ArrayList<>();
		}
//		Resource resource = new ClassPathResource("sysconfig/menus.xml");
		List<MenuResponse> menus = new ArrayList<>();

		// 创建SAXReader的对象reader
		SAXReader reader = new SAXReader();
		try {
			// 通过reader对象的read方法加载menus.xml文件,获取docuemnt对象。
			Document document = reader.read(file);
			// 通过document对象获取根节点menus
			Element patternstore = document.getRootElement();
			// 通过element对象的elementIterator方法获取迭代器
			Iterator it = patternstore.elementIterator();
			// 遍历迭代器，获取根节点中的信息
			while (it.hasNext()) {
				MenuResponse menu = new MenuResponse();
				Element pattern = (Element) it.next();
				
				// 获取menu的name
				List<Attribute> patternAttrs = pattern.attributes();
				for (Attribute attr : patternAttrs) {
					if("name".equals(attr.getName())){
						menu.setName(attr.getValue());
						break;
					}
				}
				Iterator itt = pattern.elementIterator();
				List<SubMenu> submenus = new ArrayList<>();
				//解析submenu的name，目前设计中menu只有两层。
				while (itt.hasNext()) {
					Element sub = (Element) itt.next();
					String name = sub.getStringValue().replaceAll("\n|\t|\r", "");
					SubMenu submenu = new SubMenu(name.trim());
					// 获取menu的name
					List<Attribute> subAttrs = sub.attributes();
					for (Attribute attr : subAttrs) {
						if("description".equals(attr.getName())){
							submenu.setDescription(attr.getValue());
							break;
						}
					}
					submenus.add(submenu);
				}
				menu.setSubmenus(submenus);
				menus.add(menu);
			}
		} catch (DocumentException e) {
			LOGGER.error(e);
			throw new Exception("Faild in parsering config file");
		}
		return menus;
	}

	public List<SummaryTabMap> getSummaryTabMap() {
//		Resource resource = new ClassPathResource("sysconfig/summarytabmap.json");
		List<SummaryTabMap> crs = new ArrayList<SummaryTabMap>();
		File file = FilePathUtil.getSummaryTabMapConfigPath();
		if(null==file){
			return crs;
		}
		String jsonStr = "";
		try {
			jsonStr = FileUtils.readFileToString(file);
		} catch (IOException e) {
			LOGGER.error(e);
			return crs;
		}

		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		List list = JSONArray.toList(jsonArray);
		for (Object li : list) {
			JSONObject jo = JSONObject.fromObject(li);
			SummaryTabMap cr = (SummaryTabMap) JSONObject.toBean(jo,
					SummaryTabMap.class);
			crs.add(cr);
//			LOGGER.info(cr.toString());
		}
		return crs;
	}
}
