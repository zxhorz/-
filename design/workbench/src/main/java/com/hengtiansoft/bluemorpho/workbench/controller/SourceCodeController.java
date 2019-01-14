package com.hengtiansoft.bluemorpho.workbench.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.hengtiansoft.bluemorpho.workbench.services.SourceCodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = {"SOURCECODE"}, description = "source code manager API")
@Controller
@RequestMapping(value = "/fileManager")
public class SourceCodeController extends AbstractController{

	private static final Logger LOGGER = Logger.getLogger(SourceCodeController.class);
	
	@Autowired
	private SourceCodeService sourceCodeService;
	
	
	@ApiOperation(value = "List the files in specific path", 
			nickname = "listFiles", notes = "List the files in specific path")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value ="/list",method = RequestMethod.POST)
	@ResponseBody
    public Object list(@RequestBody JSONObject json) throws ServletException {	
    	return sourceCodeService.listFiles(json);
    }

    @ApiOperation(value = "Upload files to specific folder", nickname = "upload", notes = "upload files to specific folder")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Object upload(@RequestParam("page") String page, @RequestParam("projectId") String projectId,
            @RequestParam("destination") String destination, HttpServletRequest request) {
        LOGGER.info("start upload");
        return sourceCodeService.upload(destination, page, projectId, request);
    }

	@ApiOperation(value = "Cancel Uploading files", 
			nickname = "cancelUpload", notes = "Cancel Uploading files")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value ="/cancelUpload",method = RequestMethod.POST)
	@ResponseBody
    public Object cancelUpload() {
		LOGGER.info("start upload");
		return sourceCodeService.cancelUpload();
    }
	
	@ApiOperation(value = "file preview", 
			nickname = "preview", notes = "file preview")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value ="/preview",method = RequestMethod.GET)
    public void preview(@RequestParam("path") String path,@RequestParam("projectId") String projectId,
            @RequestParam(value = "runId",required = false) String runId,@RequestParam("page") String page,
            HttpServletResponse response) throws IOException {
		sourceCodeService.preview(path,projectId,runId,page,response);
    }
	
    @ApiOperation(value = "download multi file ", 
            nickname = "downloadFiles", notes = "file download")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value ="/downloadMulti",method = RequestMethod.GET)
    public void downloadMulti(@RequestParam("paths") String[] paths,@RequestParam("projectId") String projectId,
            @RequestParam(value = "runId",required = false) String runId,@RequestParam("page") String page,
            @RequestParam("toFileName") String toFileName,HttpServletResponse response) throws IOException {
        sourceCodeService.downloadMulti(paths,projectId,runId,page,toFileName,response);
    }	

	@ApiOperation(value = "Create new folder", 
			nickname = "createFolder", notes = "create new folder with specific name")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/createFolder",method= RequestMethod.POST)
	@ResponseBody
    public Object createFolder(@RequestBody JSONObject json) {
    	return sourceCodeService.createFolder(json);
    }

	@ApiOperation(value = "Copy file to another folder", 
			nickname = "copy", notes = "copy file to another folder")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/copy",method= RequestMethod.POST)
	@ResponseBody
    public Object copy(@RequestBody JSONObject json, HttpServletRequest request) {
    	return sourceCodeService.copy(json,request);
    }

	@ApiOperation(value = "Move file with specific path", 
			nickname = "move", notes = "move file with specific path")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/move",method= RequestMethod.POST)
	@ResponseBody
    public Object move(@RequestBody JSONObject json) {
    	return sourceCodeService.move(json);
    }

	@ApiOperation(value = "Remove file or folder", 
			nickname = "remove", notes = "remove file or folder")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/remove",method= RequestMethod.POST)
	@ResponseBody
    public Object remove(@RequestBody JSONObject json) {
    	return sourceCodeService.remove(json);
    }

	@ApiOperation(value = "Rename file or folder", 
			nickname = "rename", notes = "rename file or folder")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/rename",method= RequestMethod.POST)
	@ResponseBody
    public Object rename(@RequestBody JSONObject json) {
    	return sourceCodeService.rename(json);
    }

	@ApiOperation(value = "Get content of specific file", 
			nickname = "getContent", notes = "get content of specific file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/getContent",method= RequestMethod.POST)
	@ResponseBody
    public Object getContent(@RequestBody JSONObject json) {
    	return sourceCodeService.getContent(json);
    }

	@ApiOperation(value = "Edit file", 
			nickname = "edit", notes = "edit file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/edit",method= RequestMethod.POST)
	@ResponseBody
    public Object edit(@RequestBody JSONObject json) {
    	return sourceCodeService.edit(json);
    }

	@ApiOperation(value = "Compress file or folder", 
			nickname = "compress", notes = "compress file or folder")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/compress",method= RequestMethod.POST)
	@ResponseBody
    public Object compress(@RequestBody JSONObject json) {
		return sourceCodeService.compress(json);
    }

	@ApiOperation(value = "Extract compressed file", 
			nickname = "extract", notes = "extract compressed file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/extract",method= RequestMethod.POST)
	@ResponseBody
    public Object extract(@RequestBody JSONObject json) {
		return sourceCodeService.extract(json);
    }
}
