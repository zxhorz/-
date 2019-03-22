package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.hengtiansoft.bluemorpho.workbench.domain.CustomScriptRunHistory;
import com.hengtiansoft.bluemorpho.workbench.repository.CustomScriptRunHistoryRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.CompressUtils;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileUploadUtils;

@Service
public class SourceCodeService {
	private static final Logger LOGGER = Logger.getLogger(SourceCodeService.class);
	
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private CustomScriptRunHistoryRepository customScriptRunHistoryRepository;
	private volatile boolean canUpload = true;
	
    private JSONObject error(String msg) {

        // { "result": { "success": false, "error": "msg" } }
        JSONObject result = new JSONObject();
        result.put("success", false);
        result.put("error", msg);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        return jsonObject;

    }

    private JSONObject success() {
        // { "result": { "success": true, "error": null } }
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("error", null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        return jsonObject;
    }
    
	public synchronized void cancel() {
		if (canUpload) {
			canUpload = false;
		}
	}
	
	public synchronized void openUpload() {
		if (!canUpload) {
			canUpload = true;
		}
	}
	
    private String getProjectSourceCodePath(String projectId) {
    	String projectPath = projectRepository.findPathByProjectId(projectId);
    	return FilePathUtil.getPath(projectPath, "SOURCE");
    }
    
    public Object listFiles(JSONObject json) {
        try {
            String path = json.getString("path");
            String root = getRoot(json);
            List<JSONObject> fileItems = new ArrayList<>();

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(root, path))) {
            	Path p = Paths.get(root, path);
                String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
                for (Path pathObj : directoryStream) {
                    // 获取文件基本属性
                    BasicFileAttributes attrs = Files.readAttributes(pathObj, BasicFileAttributes.class);

                    // 封装返回JSON数据
                    JSONObject fileItem = new JSONObject();
                    fileItem.put("name", pathObj.getFileName().toString());
                    fileItem.put("date", dt.format(new Date(attrs.lastModifiedTime().toMillis())));
                    fileItem.put("size", attrs.size());
                    fileItem.put("type", attrs.isDirectory() ? "dir" : "file");
                    fileItem.put("parentFolder", FilenameUtils.getBaseName(root));
                    fileItems.add(fileItem);
                }
            } catch (IOException e) {
                LOGGER.info("failed to read directorystream");
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", fileItems);
            return jsonObject;
        } catch (Exception e) {
            return error("failed to list files");
        }
    }

    public Object createFolder(JSONObject json) {
        try {

            String newPath = json.getString("newPath");
            String root = getRoot(json);
            File newDir = new File(root + newPath);
            if (!newDir.mkdir()) {
                LOGGER.info("failed to create" + newPath);
            }
            return success();
        } catch (Exception e) {
            return error("failed to list create folder");
        }
    }

	public Object move(JSONObject json) {
        try {
            String newpath = json.getString("newPath");
            JSONArray items = json.getJSONArray("items");
            String root = getRoot(json);
            for (int i = 0; i < items.size(); i++) {
                String path = items.getString(i);

                File srcFile = new File(root, path);
                File destFile = new File(root + newpath, srcFile.getName());

                if (srcFile.isFile()) {
                    FileUtils.moveFile(srcFile, destFile);
                } else {
                    FileUtils.moveDirectory(srcFile, destFile);
                }
            }
            return success();
        } catch (IOException e) {
            LOGGER.info("failed to move");
            return error("failed to move");
        }
	}

	public Object remove(JSONObject json) {
        try {
            JSONArray items = json.getJSONArray("items");
            String root = getRoot(json);
            for (int i = 0; i < items.size(); i++) {
                String path = items.getString(i);
                File srcFile = new File(root, path);
                if (!FileUtils.deleteQuietly(srcFile)) {
                    LOGGER.info("failed to remove");
                    throw new Exception("删除失败: " + srcFile.getAbsolutePath());
                }
            }
            return success();
        } catch (Exception e) {
            return error("failed to remove");
        }
	}

	public Object rename(JSONObject json) {
        try {
            String path = json.getString("item");
            String newPath = json.getString("newItemPath");
            String root = getRoot(json);
            File srcFile = new File(root, path);
            File destFile = new File(root, newPath);
            if (srcFile.isFile()) {
                FileUtils.moveFile(srcFile, destFile);
            } else {
                FileUtils.moveDirectory(srcFile, destFile);
            }
            return success();
        } catch (Exception e) {
            LOGGER.info("failed to rename");
            return error("failed to rename");
        }
	}

	public Object getContent(JSONObject json) {
        try {
            String path = json.getString("item");
            String root = getRoot(json);
            File srcFile = new File(root, path);
            JSONObject jsonObject = new JSONObject();
            if(FilenameUtils.getExtension(srcFile.getName()).equals("csv")){
                jsonObject = readCSV(srcFile);
            }
            else{
                String content = FileUtils.readFileToString(srcFile);
                jsonObject.put("result", content);
            }
            return jsonObject;
        } catch (Exception e) {
            LOGGER.info("failed to read file");
            return error("failed to read file");
        }
	}

	public Object edit(JSONObject json) {
        try {
            String path = json.getString("item");
            String content = json.getString("content");
            String root = getRoot(json);
            File srcFile = new File(root, path);
            FileUtils.writeStringToFile(srcFile, content);

            return success();
        } catch (Exception e) {
            LOGGER.info("failed to edit file");
            return error("failed to edit file");
        }
	}

	public Object upload(String destination,String page,String projectId,HttpServletRequest request) {
	    try {
            // Servlet3.0方式上传文件
        	LOGGER.info("destination");
        	if(canUpload) {
	            Collection<Part> parts = request.getParts();
	            String root = getRoot(page,projectId,null);
	            for (Part part : parts) {
	            	LOGGER.info(part.getName());
	                if (part.getContentType() != null) {  // 忽略路径字段,只处理文件类型
	                    String path = root + destination;
	                    File f = new File(path, FileUploadUtils.getFileName(part.getHeader("content-disposition")));
	                    if (!FileUploadUtils.write(part.getInputStream(), f)) {
	                        LOGGER.info("failed to upload");
	                        throw new Exception("文件上传失败");
	                    }
	                    if(extract(f)){
	                    	f.delete();
	                    }
	                }
	                if(!canUpload) {
	                	parts.clear();
	                	openUpload();
	                }
	            }
        	}else {
        		openUpload();
        	}
            return success();
        } catch (Exception e) {
            return error("failed to upload");
        }
	}

	public void preview(String path,String projectId,String runId,String page,HttpServletResponse response) throws FileNotFoundException, IOException {
	    String root = getRoot(page,projectId,runId);
	    File file =  null;
	    if(path.replace("\\\\", "/").equals("/")){
	    	file = new File(root);
	    }else{
	    	file = new File(root, path);
	    }
        
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
            return;
        }
        boolean isFolder = file.isDirectory();

        /*
         * 获取mimeType
         */
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);


        if(!isFolder){
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getName(), "UTF-8")));
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        }else {
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getName()+".zip", "UTF-8")));
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
                CompressUtils.toZip(zos,"", file);
                response.flushBuffer();
                zos.close();
            }
        }
	}

    public void downloadMulti(String[] paths, String projectId, String runId, String page, String toFileName,
            HttpServletResponse response) throws FileNotFoundException, IOException {
        String root = getRoot(page, projectId, runId);
        int length = paths.length;
        File[] files = new File[length];
        for (int i = 0; i < length; i++) {
            files[i] = new File(root,paths[i]);
            if (!files[i].exists())
                continue;
        }
        
        String mimeType = URLConnection.guessContentTypeFromName(toFileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);

        response.setHeader("Content-disposition",
                String.format("attachment; filename=\"%s\"", URLEncoder.encode(toFileName + ".zip", "UTF-8")));
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
            CompressUtils.toZip(zos, "", files);
            response.flushBuffer();
            zos.close();
        } catch (IOException e) {
            // TODO: handle exception
        }
    }
    
	public Object copy(JSONObject json, HttpServletRequest request) {
        try {
            String newpath = json.getString("newPath");
            JSONArray items = json.getJSONArray("items");
            String root = getRoot(json);
            for (int i = 0; i < items.size(); i++) {
                String path = items.getString(i);

                File srcFile = new File(root, path);
                File destFile = new File(root + newpath, srcFile.getName());

                FileCopyUtils.copy(srcFile, destFile);
            }
            return success();
        } catch (IOException e) {
            LOGGER.info("failed to copy file");
            return error("failed to copy file");
        }
	}

	public Object compress(JSONObject json) {
	       try {
	            String destination = json.getString("destination");
	            String compressedFilename = json.getString("compressedFilename");
	            JSONArray items = json.getJSONArray("items");
	            String root = getRoot(json);
	            List<File> files = new ArrayList<>();
	            for (int i = 0; i < items.size(); i++) {
	                File f = new File(root, items.getString(i));
	                files.add(f);
	            }

	            File zip = new File(root + destination, compressedFilename);

	            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip))) {
	            	CompressUtils.toZip(zos,compressedFilename, files.toArray(new File[files.size()]));
	            }
	            return success();
	        } catch (Exception e) {
                LOGGER.info("failed to compress");
	            return error("failed to compress");
	        }
	}

	public Object extract(JSONObject json) {
        try {
            String destination = json.getString("destination");
            String zipName = json.getString("item");
//            String folderName = json.getString("folderName");
            String root = getRoot(json);
            File file = new File(root, zipName);
            extract(file);
            return success();
        } catch (Exception e) {
            LOGGER.info("failed to extract");
            return error("failed to extract");
        }
	}

	private boolean extract(File file)throws Exception{
		String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
		String path = FilenameUtils.getFullPath(file.getAbsolutePath());
		switch (extension) {
		    case "zip":
		    	CompressUtils.unZip(file,path);
		    	return true;
		    case "gz":
		    	CompressUtils.unTargz(file, path);
		    	return true;
		    case "7z":
		    	CompressUtils.un7Zip(file, path);
		    	return true;
		    case "rar":
		        CompressUtils.unRar(file, path);
		        return true;
		    default: return false;
		}
	}

    public JSONObject readCSV(File file) throws IOException {
        InputStream input = new FileInputStream(file);
        JSONObject jsonObject = new JSONObject();
        CsvReader reader = new CsvReader(input, ',', Charset.forName("UTF-8"));
        // 读表头
        reader.readRecord();
        String[] headers = reader.getValues();
        List<List<String>> content = new ArrayList<List<String>>();
        while (reader.readRecord()) {
            String[] results = reader.getValues();
            content.add(Arrays.asList(results));
        }
        reader.close();
        jsonObject.put("title", headers);
        jsonObject.put("content",content);
        return jsonObject;
    }
	
    public String getRoot(JSONObject json){
    	return getRoot(json.getString("page"),json.getString("projectId"),json.getString("runId"));
//        String page = json.getString("page");
//        String root = "";
//        if(page.equals("custom"))
//            root = FilePathUtil.getScriptPath();
//        else if(page.equals("scriptOutput")){
//            String runId = json.getString("runId");
//            CustomScriptRunHistory find = customScriptRunHistoryRepository.findByRunId(runId);
//            String basedProjectId = find.getBasedProjectId();
//            String scriptName = find.getScriptName();
//            String projectPath = projectRepository.findOne(basedProjectId).getPath();
//            root = FilePathUtil.getScriptOutputPath(projectPath,scriptName,runId);
//        }
//        else {
//            String projectId= json.getString("projectId");
//            root = getProjectSourceCodePath(projectId);
//        }
//        return root;
    }
    
    public String getRoot(String page,String projectId,String runId){
        String root = "";
        if(page.equals("custom"))
            root = FilePathUtil.getScriptPath();
        else if(page.equals("scriptOutput")){
            CustomScriptRunHistory find = customScriptRunHistoryRepository.findByRunId(runId);
            String basedProjectId = find.getBasedProjectId();
            String scriptName = find.getScriptName();
            String projectPath = projectRepository.findOne(basedProjectId).getPath();
            root = FilePathUtil.getScriptOutputPath(projectPath,scriptName,runId);
        }
        else {
            root = getProjectSourceCodePath(projectId);
        }
        return root;
    }
	public Object cancelUpload() {
		cancel();
		return success();
	}
}
