package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.dto.CopybookDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.FileDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.FileItem;
import com.hengtiansoft.bluemorpho.workbench.dto.JclDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.JclStepItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ParagraphDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ParagraphUseTableInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgramDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.dto.SummaryDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.TableDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.TableItem;
import com.hengtiansoft.bluemorpho.workbench.services.SummaryService;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 8, 2018
 */
@Api(tags = { "Summary" }, description = "the project system summary API")
@Controller
@RequestMapping(value = "/summary")
public class SummaryController {
	
	private static final Logger LOGGER = Logger.getLogger(SummaryController.class);
	@Autowired
	SummaryService summaryService;

	/**
	 * get the summary detail of current project
	 */
	@ApiOperation(value = "get summary of project", nickname = "getSummaryInfo", notes = "Get the summary info of specific project")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/system", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<SummaryDetailItem>> getSummaryInfo(
			@RequestParam("projectId") int projectId) {
	    LOGGER.info("Get info from Neo4j");
		List<SummaryDetailItem> summary = summaryService
				.getSummaryInfo(projectId);

		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get program detail", nickname = "getProgramDetail", notes = "Get the detail information of program")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/programDetail", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<ProgramDetailItem>> getProgramDetail(
			@RequestParam("projectId") int projectId,
			@RequestParam("type") String type, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("query") String query) {
		Page<ProgramDetailItem> summary = summaryService.getProgramDetail(projectId, type, page, size,query);
	
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get paragraph detail", nickname = "getParagraphDetail", notes = "Get the detail information of paragraph")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/paragraphDetail", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<ParagraphDetailItem>> getParagraphDetail(
			@RequestParam("projectId") int projectId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("query") String query) {
		Page<ParagraphDetailItem> summary = summaryService.getParagraphDetail(
				projectId, page, size, query);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get table information", nickname = "getTables", notes = "Get information of tables")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/table", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<TableItem>> getTable(
			@RequestParam("projectId") int projectId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("query") String query) {
		Page<TableItem> summary = summaryService.getTableItems(projectId, page,
				size, query);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get file information", nickname = "getFiles", notes = "Get information of files")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/file", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<FileItem>> getFile(
			@RequestParam("projectId") int projectId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("query") String query) {
		Page<FileItem> summary = summaryService.getFileItems(projectId, page,
				size,query);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get copybook detail", nickname = "getCopybookDetail", notes = "Get the detail information of copybook")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/copybookDetail", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<CopybookDetailItem>> getCopybookDetail(
			@RequestParam("projectId") int projectId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("query") String query) {
		Page<CopybookDetailItem> summary = summaryService.getCopybookDetail(
				projectId, page, size, query);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get jcl detail", nickname = "getJclDetail", notes = "Get the detail information of JCL file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/jclDetail", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<JclDetailItem>> getJclDetail(
			@RequestParam("projectId") int projectId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("query") String query) {
		Page<JclDetailItem> summary = summaryService.getJclDetail(projectId,
				page, size, query);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "find index of sql logic from table tab", nickname = "findSqlLogic", notes = "Find index of sql logic from table tab")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/findSqlLogic", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	@ResponseBody
	public ResultDto<Integer> findSqlLogic(
			@RequestBody ParagraphUseTableInfo paragraphUseTableInfo) {
		int result = summaryService.findUseTableInfo(paragraphUseTableInfo);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, result);
	}

	@ApiOperation(value = "get sql logic", nickname = "getSqlLogic", notes = "Get the detail information of sql logic")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/sqlLogic", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<ParagraphUseTableInfo>> getSqlLogic(
			@RequestParam("projectId") int projectId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("query") String query) {
		Page<ParagraphUseTableInfo> summary = summaryService.getSqlLogic(
				projectId, page, size, query);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get jcl steps info", nickname = "getJclStep", notes = "Get the detail information of JCL steps")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/jclStep", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<JclStepItem>> getJclStep(
			@RequestParam("projectId") int projectId,
			@RequestParam("nodeId") String nodeId,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<JclStepItem> summary = summaryService.getJclSteps(projectId,
				nodeId, page, size);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get program names list with copybook nodeId", nickname = "getProgramWithCpyId", notes = "Get program names list with copybook nodeId")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/geProgramWithCpyId", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<ProgramDetailItem>> geProgramWithCpyId(
			@RequestParam("projectId") int projectId,
			@RequestParam("cpyId") String cpyId,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<ProgramDetailItem> summary = summaryService.getProgramWithCpyId(
				projectId, cpyId, page, size);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get file source code with file path", nickname = "getSourceCode", notes = "Get file source code with file path")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/getSourceCode", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> getSourceCode(
			@RequestParam("location") String location) {
		String result = summaryService.getSourceCode(location);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, result);
	}

	@ApiOperation(value = "get table detail information", nickname = "getTableDetail", notes = "Get the detail information of tables")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/tableDetail", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<TableDetailItem>> getTableDetail(
			@RequestParam("projectId") int projectId,
			@RequestParam("nodeId") String nodeId,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<TableDetailItem> summary = summaryService.getTableDetailItems(
				projectId, nodeId, page, size);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get paragraphs which use the table", nickname = "getParagraphUseTable", notes = "Get paragraphs which use the table")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/useTable", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<ParagraphUseTableInfo>> getParagraphUseTable(
			@RequestParam("projectId") int projectId,
			@RequestParam("nodeId") String nodeId,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<ParagraphUseTableInfo> summary = summaryService
				.getParagraphWithTable(projectId, nodeId, page, size);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "get file detail information", nickname = "getFileDetail", notes = "Get the detail information of files")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/fileDetail", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<FileDetailItem>> getFileDetail(
			@RequestParam("projectId") int projectId,
			@RequestParam("nodeId") String nodeId,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<FileDetailItem> summary = summaryService.getFileDetailItems(
				projectId, nodeId, page, size);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, summary);
	}

	@ApiOperation(value = "download doc", nickname = "docDownload", notes = "Download doc")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/detailDoc", method = RequestMethod.GET)
	@ResponseBody
	public void downloadSystemDocumentation(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("projectId") String projectId) throws UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");
		summaryService.downLoadSystemDocumentationDoc(response, projectId);
	}

	@ApiOperation(value = "get html content", nickname = "getPrintHtmlContent", notes = "get html content")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/printContent", method = RequestMethod.POST)
	@ResponseBody
	public void getPrintContent(@RequestParam("projectId") String projectId,HttpServletResponse response) {
	    String pdfPath = summaryService.printSystemDocumentationPdf(response, projectId);
		File file = new File(pdfPath);
		try {
            FilePathUtil.download(file, response);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
	}	
}
