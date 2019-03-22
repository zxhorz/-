package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;

@Component
public class FileMappingUtil {
    private static final Logger LOGGER = Logger.getLogger(FileMappingUtil.class);
    
	@Autowired
	ProjectRepository projectRepository;

	private Map<String, String> fileMap = new HashMap<>();

	public void initFileMap(String path, String ruleName) {

			String mapFile = path + "/" + ruleName + ".xml";

			// 创建SAXReader的对象reader
			SAXReader reader = new SAXReader();
			try {
				// 通过reader对象的read方法加载books.xml文件,获取docuemnt对象。
				Document document = reader.read(new File(mapFile));
				// 通过document对象获取根节点bookstore
				Element patternstore = document.getRootElement();
				// 通过element对象的elementIterator方法获取迭代器
				Iterator it = patternstore.elementIterator();
				// 遍历迭代器，获取根节点中的信息
				while (it.hasNext()) {

					Element pattern = (Element) it.next();
					// 获取book的属性名以及 属性值
//					List<Attribute> patternAttrs = pattern.attributes();
//					for (Attribute attr : patternAttrs) {
//						System.out.println("属性名：" + attr.getName() + "--属性值："
//								+ attr.getValue());
//					}
					Iterator itt = pattern.elementIterator();
					String key = null;
					String value = null;
					while (itt.hasNext()) {
						Element bookChild = (Element) itt.next();
						if ("filetype".equals(bookChild.getName())) {
							key = bookChild.getStringValue();
						} else if ("rules".equals(bookChild.getName())) {
							value = bookChild.getStringValue();
						}
						if (null != key && null != value) {
							fileMap.put(key, value);
							key = null;
							value = null;
						}
					}
				}
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
			    LOGGER.error(e);
			}
	}

	public Map<String, List<String>> getFileByType(String projectId,
			String fileType) {
		Project project = projectRepository.findOne(projectId);

		if (null != project){
			String configPath = FilePathUtil.getPath(project.getPath(),"CONFIG");
			initFileMap(configPath,project.getFileMapping());			
		}
		String sourcePath = FilePathUtil.getPath(project.getPath(),"SOURCE");
//		Map<String, List<String>> fileMap = mapFileType(sourcePath);
		return mapFileType(sourcePath);
	}

	private Map<String, List<String>> mapFileType(String sourcePath) {
		Map<String, List<String>> fileMap = new HashMap<>();
		File file = new File(sourcePath);
		File[] files = file.listFiles();
		for(int i =0;i<files.length;i++){
			if(files[i].isDirectory()){
				String fileType = checkFileType(files[i].getAbsolutePath());
				List<String> fileList = getAllFiles(files[i].getAbsolutePath());
				fileMap.put(fileType, fileList);
			}	
		}
		return fileMap;
	}

	private String checkFileType(String absolutePath) {
		String path = StringUtils.replace(absolutePath, "\\", "/");
		for(Entry<String,String> map:fileMap.entrySet()){
			if(path.endsWith(map.getValue())){
				return map.getKey();
			}
		}
		return null;
	}

	private List<String> getAllFiles(String path){
		List<String> fileList = new ArrayList<>();
		File file = new File(path);
		File[] files = file.listFiles();
		for(int i =0;i<files.length;i++){
			if(files[i].isDirectory()){
				fileList.addAll(getAllFiles(files[i].getAbsolutePath()));
			}else{
				fileList.add(files[i].getAbsolutePath());
			}
		}
		return fileList;
	}
}
