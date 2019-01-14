package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class FileStatusUtil {
	
	private static final Logger logger = Logger.getLogger(FileStatusUtil.class);
	private static final String FILESTATUS = "/filestatus.txt";
	private static final String LAST_FILESTATUS = "/last_filestatus.txt";
	private static final String CONFIG = "CONFIG";
	private static final String SOURCE = "SOURCE";
//	private static int version = 0;
	
	public static String checkCode(String path) {
		//traverse all file to get last modified time
		String sourcePath = FilePathUtil.getPath(path, SOURCE);
		String fileStatusPath = FilePathUtil.getPath(path, CONFIG);
		Map<String, String> oldFileList = getOldFileStatus(fileStatusPath
				+ FILESTATUS);
		Map<String,String> newFileList = getAllFileStatus(sourcePath);
		String version = getVersion(fileStatusPath);
		int versionInt = Integer.valueOf(version).intValue();
		if(null==oldFileList){
			if(null==newFileList){
				return version;
			}else
			{
				writeAllFileStatus(newFileList,fileStatusPath,versionInt + 1);
				return String.valueOf(versionInt + 1);				
			}
		}
		if(null==newFileList){
			writeAllFileStatus(newFileList,fileStatusPath,versionInt + 1);
			return String.valueOf(versionInt + 1);		
		}
		boolean codeUpdate = compareFileStatus(oldFileList,newFileList);
		if(codeUpdate){
			writeAllFileStatus(newFileList,fileStatusPath,versionInt + 1);
			return String.valueOf(versionInt + 1);
		}
		return version;
	}
	
	public static void lastFileStatus(String projectPath) {
		String path = FilePathUtil.getPath(projectPath, CONFIG);
		File nowFileStatus = new File(path + FILESTATUS);
		File lastFileStatus = new File(path + LAST_FILESTATUS);
		if (lastFileStatus.exists()) {
			lastFileStatus.delete();
		}
		try {
			FileUtils.copyFile(nowFileStatus, lastFileStatus);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public static String getSearchVersion(String path){
		return getVersion(path);
	}
	
	public static Date getLastModifiedTime(String path){
		File file = new File(path);
		if(!file.exists()){
			return null;
		}
		Long lastModifyDate = getLastModified(file,file.lastModified());

		return new Date(lastModifyDate);
	}
	
	private static Long getLastModified(File file, long lastDate) {
		long modify = lastDate;
		if(file.isFile()) {
			return modify>file.lastModified()?modify:file.lastModified();
		}else {
			modify = modify>file.lastModified()?modify:file.lastModified();
			for(String path:file.list()) {
				File f = new File(file.getPath()+"/"+ path);
				modify = getLastModified(f,modify);
			}
		}
		return modify;
	}

	public static void wirteNewSearchVersion(String path, String fileStatusPath,int version ){
		//codesearch只针对COBOL文件，如果后续查询可以扩大到整个sourcecode文件夹，那么getAllFileStatus的参数需要更改为sourcePath。
		String sourcePath = FilePathUtil.getPath(path,"SOURCE");
		String codePath = FilePathUtil.getPath(sourcePath, "COBOL");
		Map<String,String> newFileList = getAllFileStatus(codePath);
		writeAllFileStatus(newFileList,fileStatusPath,version);
	}

	public static Map<String,List<String>> diffFileList(String path, String outputPath) {
		//准备codesearch使用的diff，只针对COBOL。
		String sourcePath = FilePathUtil.getPath(path,"SOURCE");
		String codePath = FilePathUtil.getPath(sourcePath, "COBOL");
		Map<String,String> oldFileList = getOldFileStatus(outputPath);
		Map<String,String> newFileList = getAllFileStatus(codePath);
		List<String> addFileList = new ArrayList<>();
		List<String> deleteFileList = new ArrayList<>();
		List<String> updateFileList = new ArrayList<>();

		for(Entry<String,String> map:oldFileList.entrySet()){
			if(!newFileList.containsKey(map.getKey())){
				deleteFileList.add(map.getKey());
			}else{
				String newTime = newFileList.get(map.getKey());
				if(Long.valueOf(newTime).longValue()!=Long.valueOf(map.getValue()).longValue()){
					addFileList.add(map.getKey());
					updateFileList.add(map.getKey());
				}
			}
		}

		for(Entry<String,String> m:newFileList.entrySet()){
			if(!oldFileList.containsKey(m.getKey())){
				addFileList.add(m.getKey());
			}
		}	
		Map<String,List<String>> diff = new HashMap<>();
		diff.put("ADD", addFileList);
		diff.put("DELETE", deleteFileList);		
		diff.put("UPDATE", updateFileList);
		return diff;
	}
	
	public static Map<String,List<String>> diffFiles(String path, String outputPath) {
		String sourcePath = FilePathUtil.getPath(path,"SOURCE");
//		String codePath = FilePathUtil.getPath(sourcePath, "COBOL");
		Map<String,String> oldFileList = getOldFileStatus(outputPath);
		Map<String,String> newFileList = getAllFileStatus(sourcePath);
		List<String> addFileList = new ArrayList<>();
		List<String> deleteFileList = new ArrayList<>();
		List<String> updateFileList = new ArrayList<>();

		for(Entry<String,String> map:oldFileList.entrySet()){
			if(!newFileList.containsKey(map.getKey())){
				deleteFileList.add(map.getKey());
			}else{
				String newTime = newFileList.get(map.getKey());
				if(Long.valueOf(newTime).longValue()!=Long.valueOf(map.getValue()).longValue()){
					addFileList.add(map.getKey());
					updateFileList.add(map.getKey());
				}
			}
		}

		for(Entry<String,String> m:newFileList.entrySet()){
			if(!oldFileList.containsKey(m.getKey())){
				addFileList.add(m.getKey());
			}
		}
		Map<String,List<String>> diff = new HashMap<>();
		diff.put("ADD", addFileList);
		diff.put("DELETE", deleteFileList);
		diff.put("UPDATE", updateFileList);
		return diff;
	}

	private static boolean compareFileStatus(Map<String, String> oldFileList,
			Map<String, String> newFileList) {
		if(oldFileList.size()!=newFileList.size()){
			return true;
		}
		for(Entry<String,String> map:oldFileList.entrySet()){
			if(!newFileList.containsKey(map.getKey())){
				return true;
			}else{
				String newTime = newFileList.get(map.getKey());
				if(Long.valueOf(newTime).longValue()!=Long.valueOf(map.getValue()).longValue()){
					return true;
				}
			}
		}

		for(Entry<String,String> m:newFileList.entrySet()){
			if(!oldFileList.containsKey(m.getKey())){
				return true;
			}
		}
		return false;
	}

	private static void writeAllFileStatus(Map<String, String> newFileList,
			String fileStatusPath,int version) {
		File file = new File(fileStatusPath);
		if(!file.exists()){
			file.mkdirs();
		}
		try {
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(new File(fileStatusPath + FILESTATUS)));
	        BufferedWriter writer = new BufferedWriter(write);
	        String content = null;
	        writer.write(String.valueOf(version));
	        if(null!=newFileList && newFileList.size()>0){
	            writer.newLine();
	            for(Entry<String,String> map: newFileList.entrySet()){
	            	content = map.getKey() + "||" + map.getValue();
		            writer.write(content);
		            writer.newLine();
	            }}
	            writer.flush(); 
	            write.close();
	            writer.close();		            	
		} catch (IOException e) {
			logger.error(e);
		}		
	}

	private static Map<String, String> getOldFileStatus(String fileStatusPath) {
		File file = new File(fileStatusPath);
		if(!file.exists()){
			return null;
		}
		try {
			InputStream f1 = new FileInputStream(file);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(f1));
			String line1 = null;
			try {
				line1 = br1.readLine();
				if(null==line1){
					br1.close();
					f1.close();
					return null;
				}
				Map<String,String> fileList = new HashMap<>();
				while ((line1 = br1.readLine()) != null) {
					String[] str = line1.split("\\|\\|");
					fileList.put(str[0], str[1]);
				}
				br1.close();
				f1.close();
				return fileList;
			} catch (IOException e) {
				logger.error(e);
				return null;
			}
		} catch (FileNotFoundException e) {
			logger.error(e);
			return null;
		}
	}

	private static Map<String, String> getAllFileStatus(String sourcePath) {
		Map<String,String> fileList = new HashMap<>();
		File file = new File(sourcePath);
		if(!file.exists()){
			return null;
		}
		if(file.isDirectory()){
			getAllFileStatus(fileList,file);
		}else{
			fileList.put(file.getAbsolutePath(), String.valueOf(file.lastModified()));
		}
		return fileList;
	}

	private static void getAllFileStatus(Map<String, String> fileList,
			File file) {
		File[] files = file.listFiles();
		for(File f: files){
			if(f.isDirectory()){
				getAllFileStatus(fileList,f);
			}else{
				fileList.put(f.getAbsolutePath(), String.valueOf(f.lastModified()));
			}
		}
	}

	private static String getVersion(String fileStatusPath) {
		File file = new File(fileStatusPath+FILESTATUS);
		if(!file.exists()){
			return "0";
		}
		try {
			InputStream f1 = new FileInputStream(file);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(f1));
			String line1 = null;
			try {
				line1 = br1.readLine();
				if(null==line1){
					br1.close();
					f1.close();
					return "0";
				}
				br1.close();
				f1.close();
				return line1;
			} catch (IOException e) {
				logger.error(e);
				return "0";
			}
		} catch (FileNotFoundException e) {
			logger.error(e);
			return "0";
		}
	}

	public static String autoTagTypeMapping(String type) {
		String mappingType = "Program";
		switch (type.toUpperCase()) {
		case "COBOL":
			mappingType = "Program";
			break;
		case "COPYBOOK":
			mappingType = "Copybook";
			break;
		case "JOB":
		case "PROC":
			mappingType = "JclFile";
			break;
		default:
			mappingType = "Program";
			break;
		}
		return mappingType;
	}
}
