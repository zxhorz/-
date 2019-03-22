package com.hengtiansoft.bluemorpho.workbench.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.hengtiansoft.bluemorpho.workbench.dto.CheckMissingResult;
import com.hengtiansoft.bluemorpho.workbench.dto.ControlFlowDto;
import com.hengtiansoft.bluemorpho.workbench.dto.DocDownloadInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.DependencyDto;
import com.hengtiansoft.bluemorpho.workbench.dto.FileFolderTreeResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.FileStructureNode;
import com.hengtiansoft.bluemorpho.workbench.dto.ParagraphSourceCodeDetail;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.CodeBrowserService;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = { "CodeBrowser" }, description = "the CodeBrowser API")
@Controller
@RequestMapping(value = "/codebrowser")
public class CodeBrowserController extends AbstractController {
	private static final Logger LOGGER = Logger
			.getLogger(CodeBrowserController.class);
	@Autowired
	CodeBrowserService codeBrowserService;

	@ApiOperation(value = "get source code directory", nickname = "getFolderTree", notes = "Get source code directory with the specific projectID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/getFolderTree", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<FileFolderTreeResponse> getFolderTree(
			@RequestParam("projectId") String projectId) {
		LOGGER.info("Get source code directory.");
		FileFolderTreeResponse fft = codeBrowserService
				.getFolderTree(projectId);
		return ResultDtoFactory.toAck("S", fft);

	}

	@ApiOperation(value = "get source code with selected file", nickname = "getSourceCode", notes = "Get source code with selected file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/getSourceCode", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<String> getSourceCode(
			@RequestParam("projectId") String projectId,
			@RequestParam("filePath") String filePath) {
		LOGGER.info("Get source code with selected file.");
		if(null==filePath||filePath.isEmpty()){
			return ResultDtoFactory.toAck("S", "");
		}
		String content = codeBrowserService.getSourceCode(projectId, filePath);
		return ResultDtoFactory.toAck("S", content);
	}
	
	@ApiOperation(value = "get source code with selected paragraph", nickname = "getParaSourceCode", notes = "Get source code with paragraph file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/getParaSourceCode", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<ParagraphSourceCodeDetail> getParaSourceCode(
			@RequestParam("projectId") String projectId,
			@RequestParam("filePath") String filePath,
			@RequestParam("startLine") int startLine,
			@RequestParam("endLine") int endLine) {
		LOGGER.info("Get source code with selected paragraph.");
		ParagraphSourceCodeDetail content = codeBrowserService
				.getParaSourceCode(projectId, filePath, startLine, endLine);
		return ResultDtoFactory.toAck("S", content);
	}

	@ApiOperation(value = "get control flow with selected file", nickname = "getControlFlow", notes = "Get control flow with selected file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/controlFlow", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<List<ControlFlowDto>> getControlFlow(
			@RequestParam("projectId") String projectId,
			@RequestParam("fileName") String fileName) {
		List<ControlFlowDto> controlFlows = codeBrowserService.getControlFlow(
				projectId, fileName);
		return ResultDtoFactory.toAck("S", controlFlows);
	}
	
	@ApiOperation(value = "get file structure with selected file", nickname = "getFileStructure", notes = "Get file structure with selected file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/fileStructure", method = RequestMethod.GET)
	@ResponseBody
	public String getFileStructure(
			@RequestParam("projectId") String projectId,
			@RequestParam("fileName") String fileName) {
		List<FileStructureNode> fileStructureNodes = codeBrowserService.getFileStructure(
				projectId, fileName);
		ResultDto<List<FileStructureNode>> dto = ResultDtoFactory.toAck("S", fileStructureNodes);
		return JSON.toJSON(dto).toString();
	}
	
	@ApiOperation(value = "get dependency with selected file", nickname = "getDependency", notes = "Get dependency with selected file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/getDependency", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<DependencyDto> getDependency(@RequestParam("projectId") String projectId, @RequestParam("programName") String programName) {
		DependencyDto dependency = codeBrowserService.getDependency(projectId, programName);
		return ResultDtoFactory.toAck("", dependency);
	}

    @ApiOperation(value = "get missing files", nickname = "get missing files", notes = "Get missing files")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
    @RequestMapping(value = "/checkMissing", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<CheckMissingResult> checkMissing(@RequestParam("projectId") String projectId) {
        CheckMissingResult result = codeBrowserService.checkMissing(projectId);
        if (result != null)
            return ResultDtoFactory.toAck("S", result);
        else
            return ResultDtoFactory.toAck("F", result);
    }
    
    @ApiOperation(value = "download current controlFlow documentation", nickname = "downloadCur CtrlFlow Doc", notes = "download current controlFlow documentation")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
    @RequestMapping(value = "/downloadCurCtrlFlowDoc", method = RequestMethod.POST)
    @ResponseBody
	public void downloadCurCtrlFlowDoc(HttpServletRequest request, HttpServletResponse response,
			DocDownloadInfo docInfo) throws UnsupportedEncodingException {
    	request.setCharacterEncoding("UTF-8");
		codeBrowserService.downloadCurCtrlFlowDoc(docInfo, response);
	}
    
    @ApiOperation(value = "download current fileStructure documentation", nickname = "downloadCur fileStructure Doc", notes = "download current fileStructure documentation")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
    @RequestMapping(value = "/downloadCurFileStructureDoc", method = RequestMethod.POST)
    @ResponseBody
	public void downloadCurFileStructureDoc(HttpServletRequest request, HttpServletResponse response,
			DocDownloadInfo docInfo) throws UnsupportedEncodingException {
    	request.setCharacterEncoding("UTF-8");
		codeBrowserService.downloadCurFileStructureDoc(docInfo, response);
	}
    
    @ApiOperation(value = "download current dependency documentation", nickname = "download current dependency Doc", notes = "download current dependency documentation")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
    @RequestMapping(value = "/downloadCurDependencyDoc", method = RequestMethod.POST)
    @ResponseBody
	public void downloadCurDependencyDoc(HttpServletRequest request, HttpServletResponse response,
			DocDownloadInfo docInfo) throws UnsupportedEncodingException {
    	request.setCharacterEncoding("UTF-8");
		codeBrowserService.downloadCurDependencyDoc(docInfo, response);
	}

    @ApiOperation(value = "download one program documentation", nickname = "download one program Doc", notes = "download one program documentation")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
    @RequestMapping(value = "/downloadOneProgramDoc", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public void downloadOneProgramDoc(HttpServletRequest request, HttpServletResponse response,
            DocDownloadInfo docInfo)
            throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        String docFilePath = codeBrowserService.downloadOneProgramDoc(docInfo, response)+".doc";
        File docFile = new File(docFilePath);
        try {
            FilePathUtil.download(docFile, response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error(e);
        }
    }
    
    @ApiOperation(value = "download one program documentation", nickname = "download one program Doc", notes = "download one program documentation")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
    @RequestMapping(value = "/printOneProgramPdf", method = RequestMethod.POST)
    @ResponseBody
    public void getPrintContent(DocDownloadInfo docInfo,HttpServletResponse response) {
        String pdfPath = codeBrowserService.downloadOneProgramDoc(docInfo,response)+".pdf";
        File file = new File(pdfPath);
        try {
            FilePathUtil.download(file, response);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }   
   
    @ApiOperation(value = "get external dependencies table data", nickname = "get external dependencies", notes = "get external dependencies")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
    @RequestMapping(value = "/getExternalDependencies", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String[][]> getExternalDependencies(@RequestParam("projectId") String projectId, @RequestParam("programName") String programName) {
        String[][] dependency = codeBrowserService.getExternalDependencies(projectId, programName);
        return ResultDtoFactory.toAck("", dependency);
    }
}
