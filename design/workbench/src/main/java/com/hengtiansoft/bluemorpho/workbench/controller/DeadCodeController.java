package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.dto.DeadCodeResult;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.DeadCodeService;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 6, 2018
 */
@Api(tags = { "DeadCode" }, description = "the dead code API")
@Controller
@RequestMapping(value = "/deadCode", produces = { "application/json" }, consumes = { "application/json" })
public class DeadCodeController {
	@Autowired
	private DeadCodeService deadCodeService;
	private static final Logger LOGGER = Logger
			.getLogger(DeadCodeController.class);

	@ApiOperation(value = "Get summary of dead code analysis", nickname = "getDeadCodeSummary", notes = "Get the summary info of dead code analysis on the project with specified project ID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/summary", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<DeadCodeResult> getDeadCodeSummary(
			@RequestParam("projectId") int projectId) {
		LOGGER.info("Get summary of Dead Code Analysis");
		DeadCodeResult dcr = deadCodeService.getDeadCodeSummary(projectId);
		String message = "";

		if (null == dcr) {
			message = "Error in getting summary of dead code analysis";
		}
		return ResultDtoFactory.toAck(message, dcr);
	}

}
