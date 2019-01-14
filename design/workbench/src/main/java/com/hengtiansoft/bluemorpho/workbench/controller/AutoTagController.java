package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.dto.AutoTagResult;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.AutoTagService;

/**
 * @Description: tag自动化推荐
 * @author gaochaodeng
 * @date Aug 21, 2018
 */
@Api(tags = { "AutoTag" }, description = "the auto tag API")
@Controller
@RequestMapping(value = "/autoTag")
public class AutoTagController extends AbstractController {
	@Autowired
	AutoTagService autoTagService;

	@ApiOperation(value = "get auto recommand tags", nickname = "getAutoTags", notes = "get auto recommand tags")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/getAutoTags", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<AutoTagResult>> getAutoTag(
			@RequestParam("outputPath") String outputPath) {
		List<AutoTagResult> results = autoTagService
				.getAutoTagResults(outputPath);
		return ResultDtoFactory.toAck("S", results);
	}

	@ApiOperation(value = "auto tag feedback", nickname = "feedback", notes = "auto tag feedback, including file and tags update")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/feedback", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> feedback(
			@RequestParam("projectId") String projectId,
			@RequestParam("name") String name,
			@RequestParam("type") String type, @RequestParam("tag") String tag) {
		autoTagService.feedback(projectId, name, type, tag);
		return ResultDtoFactory.toAck("S", "S");
	}
}
