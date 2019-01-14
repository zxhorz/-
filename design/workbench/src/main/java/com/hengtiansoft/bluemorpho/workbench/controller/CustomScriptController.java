package com.hengtiansoft.bluemorpho.workbench.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.dto.CustomScriptRunHistoryResult;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import com.hengtiansoft.bluemorpho.workbench.domain.Script;
import com.hengtiansoft.bluemorpho.workbench.domain.ScriptHistoryDetail;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.CustomScriptService;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 25, 2018 1:41:10 PM
 */
@Api(tags = { "CustomScript" }, description = "the CustomScript API")
@Controller
@RequestMapping(value = "/customScript")
public class CustomScriptController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(CustomScriptController.class);
	
	@Autowired
	private CustomScriptService customScriptService;
	
	/*@ApiOperation(value = "exec the custom script", nickname = "runScript", notes = "Execute the specific script")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/run", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<String>> runScript(int projectId, String scriptName, String commandLineOptions) {
		String msg = customScriptService.runScript(projectId, scriptName, commandLineOptions);
		return ResultDtoFactory.toAck(msg);
	}*/
	
	@ApiOperation(value = "add custom script history", nickname = "addScriptHistory", notes = "Add custom script history")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/addHistory", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> addScriptHistory(int projectId, String scriptName, String commandLineOptions) {
		String runId = customScriptService.addScriptHistory(projectId, scriptName, commandLineOptions);
		return ResultDtoFactory.toAck(null, runId);
	}
	
	@ApiOperation(value = "exec the custom script", nickname = "runScript", notes = "Execute the specific script")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/run", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> runScript(String runId) {
		String msg = customScriptService.runScript(runId);
		return ResultDtoFactory.toAck(msg);
	}
	
	@ApiOperation(value = "get output list", nickname = "getOutputList", notes = "get the output list of script excution")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/getOutputList", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<String>> getOutputList(String runId) {
		List<String> outputs = customScriptService.getOutputList(runId);
		return ResultDtoFactory.toAck(null, outputs);
	}
	
    @ApiOperation(value = "get script list", nickname = "getScriptList", notes = "get script list")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "An array of scripts"),
            @ApiResponse(code = 200, message = "Unexpected error", response = Error.class) })
    @RequestMapping(value = "/customScriptList", method = RequestMethod.GET, produces = { "application/json" })
    @ResponseBody
    public ResultDto<Page<Script>> scriptList(@RequestParam("page") int page,
            @RequestParam("size") int size) {
        Page<Script> results = customScriptService.getScriptList(page,size);
        return ResultDtoFactory.toAck("", results);
    }
    
    @ApiOperation(value = "launch script", nickname = "scriptLaunch", notes = "launch the specific script and get commond")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/customScriptLaunch", method = RequestMethod.GET,produces = { "application/json" })
    @ResponseBody
    public ResultDto<String> scriptLaunch(@RequestParam("scriptName") String scriptName){
        String result = customScriptService.scriptLaunch(scriptName);
        return ResultDtoFactory.toAck("", result);
    }
    
    @ApiOperation(value = "get script history list", nickname = "scriptHistory", notes = "view script history")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/customScriptHistory", method = RequestMethod.GET, produces = { "application/json" })
    @ResponseBody
    public ResultDto<List<CustomScriptRunHistoryResult>> scriptHistory() {
        List<CustomScriptRunHistoryResult> result = customScriptService.scriptHistory();
        return ResultDtoFactory.toAck("", result);
    }

//    @ApiOperation(value = "open output files", nickname = "scriptOutputOpen", notes = "open script output files")
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
//    @RequestMapping(value = "/scriptOutputOpen", method = RequestMethod.GET, produces = { "application/json" })
//    @ResponseBody
//    public ResultDto<String> scriptOutputOpen(@RequestParam("runId") String runId,
//            @RequestParam("fileName") String fileName) {
//        String msg = customScriptService.scriptOutputOpen(runId, fileName);
//        return ResultDtoFactory.toAck(msg);
//    }

    @ApiOperation(value = "delete custom script history", nickname = "deleteScriptHistory", notes = "Delete custom script history")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/deleteHistory", method = RequestMethod.GET, produces = { "application/json" })
    @ResponseBody
    public ResultDto<String> deleteScriptHistory(@RequestParam("runId") String runId) {
        customScriptService.deleteScriptHistory(runId);
        return ResultDtoFactory.toAck(null);
    }

    @ApiOperation(value = "script history detail", nickname = "scriptHistoryDetail", notes = "view custom script history detail")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/scriptHistoryDetail", method = RequestMethod.GET, produces = { "application/json" })
    @ResponseBody
    public ResultDto<ScriptHistoryDetail> scriptHistoryDetail(@RequestParam("runId") String runId) {
        ScriptHistoryDetail scriptHistoryDetail = customScriptService.scriptHistoryDetail(runId);
        return ResultDtoFactory.toAck("", scriptHistoryDetail);
    }
    
    @ApiOperation(value = "delete script", nickname = "deleteScript", notes = "delete script")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/deleteScript", method = RequestMethod.GET, produces = { "application/json" })
    @ResponseBody
    public ResultDto<String> deleteScript(@RequestParam("scriptName") String scriptName) {
        if(customScriptService.deleteScript(scriptName))
            return ResultDtoFactory.toAck("Successfully deleted script:" + scriptName);
        else
            return ResultDtoFactory.toAck("failed to delete script:" + scriptName);
    }
    
    @ApiOperation(value = "download File", nickname = "downloadFile", notes = "download File")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/downloadFile", method = RequestMethod.POST)
    @ResponseBody
    public void downloadFile(@RequestParam("runId") String runId,@RequestParam("fileName") String fileName,HttpServletResponse response) throws IOException{
        String path = customScriptService.downloadFile(runId, fileName);
        File file = new File(path);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
            return;
        }
        
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getName(), "UTF-8")));
        response.setContentLength((int) file.length());

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
    }
    
}
