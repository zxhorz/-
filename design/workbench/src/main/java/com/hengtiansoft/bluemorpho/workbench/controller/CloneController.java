package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.model.CloneDiffInfo;
import com.hengtiansoft.bluemorpho.model.CloneTierInGroup;
import com.hengtiansoft.bluemorpho.workbench.dto.CloneDiffRequest;
import com.hengtiansoft.bluemorpho.workbench.dto.CloneResult;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.CloneService;

@Api(tags = { "Clone" }, description = "the clone API")
@Controller
@RequestMapping(value = "/clone")
public class CloneController extends AbstractController {

	private static final Logger LOGGER = Logger
			.getLogger(CloneController.class);

	@Autowired
	CloneService cloneService;

	@ApiOperation(value = "Get summary of clone analysis", nickname = "getCloneSummary", notes = "Get the summary info of clone analysis on the project with specified project ID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/summary", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<CloneResult> getCloneSummary(
			@RequestParam("projectId") int projectId) {
		LOGGER.info("Get summary of Clone Analysis");

		CloneResult cr = cloneService.getCloneSummary(projectId);

		String message = "";

		if (null == cr) {
			message = "Error in getting summary of clone analysis";
		}
		return ResultDtoFactory.toAck(message, cr);
	}

	@ApiOperation(value = "Get group list of clone analysis", nickname = "getGroupList", notes = "Get group list from clone analysis result")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/groupList", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<CloneTierInGroup>> getGroupList(
			@RequestParam("projectId") int projectId) {
		LOGGER.info("Get group list of clone analysis");
		List<CloneTierInGroup> cr = cloneService.getTierInGroup(projectId);
		return ResultDtoFactory.toAck("", cr);
	}

	@ApiOperation(value = "Get member info of a group", nickname = "getGroupList", notes = "Get member info of a group, prepare to diff analysis")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/prepareDiff", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<CloneDiffInfo> getGroupForDiff(
			@RequestParam("projectId") int projectId,
			@RequestParam("groupNo") int groupNo) {
		LOGGER.info("Get member info of a group");
		CloneDiffInfo cr = cloneService.getGroupForDiff(projectId,
				groupNo);
		return ResultDtoFactory.toAck("", cr);
	}

	@ApiOperation(value = "Get source code of selected two paragraphs", nickname = "getParaSourceCode", notes = "Get source code of selected two paragraphs")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/getSourceCode", method = RequestMethod.POST, produces = { "application/json" },
	consumes= { "application/json" })
	@ResponseBody
	public ResultDto<List<String>> getParaSourceCode(@RequestBody CloneDiffRequest diffInfo) {
		LOGGER.info("Get member info of a group");
		List<String> cr = cloneService.getParaSourceCode(diffInfo);
		return ResultDtoFactory.toAck("", cr);
	}

	@ApiOperation(value = "Diff analysis", nickname = "diff", notes = "Diff analysis for selected two paragraphs, and return tier.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/diff", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResultDto<Integer> diff(@RequestParam("projectId") int projectId,
			@RequestParam("leftParaName") String leftParaName,
			@RequestParam("rightParaName") String rightParaName) {
		LOGGER.info("Get member info of a group");
		int cr = cloneService.diff(projectId, leftParaName, rightParaName);
		return ResultDtoFactory.toAck("", cr);
	}

	@ApiOperation(value = "get all project clone percentage", nickname = "projectClonePercentage", notes = "Get all project clone percentage")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/clonePercentage", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> getTotalClone(
			@RequestParam("projectId") int projectId) {
		LOGGER.info("Get all project clone percentage");
		String cr = cloneService.getTotalClone(projectId);
		return ResultDtoFactory.toAck("", cr);
	}
}
