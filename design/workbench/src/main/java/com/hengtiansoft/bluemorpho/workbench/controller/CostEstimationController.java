package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hengtiansoft.bluemorpho.workbench.dto.CostEstimationResult;
import com.hengtiansoft.bluemorpho.workbench.dto.CostExcelView;
import com.hengtiansoft.bluemorpho.workbench.dto.CostParameters;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.CostEstimationService;

/**
 * @Description: Cost Estimation
 * @author gaochaodeng
 * @date Jun 29, 2018
 */
@Api(tags = { "CostEstimation" }, description = "the CostEstimation API")
@Controller
@RequestMapping(value = "/costEstimation")
public class CostEstimationController extends AbstractController {
	private static final Logger LOGGER = Logger
			.getLogger(CostEstimationController.class);
	@Autowired
	CostEstimationService costEstimationService;

	@ApiOperation(value = "Get data of cost estimation", nickname = "getCostEstimation", notes = "Get data of cost estimation with specified project ID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/getCostData", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	@ResponseBody
	public ResultDto<CostEstimationResult> getCostData(
			@RequestBody CostParameters parameters) {
		LOGGER.info("Get data of cost estimation");

		CostEstimationResult cr = costEstimationService
				.getCostEstimationResult(parameters);
		String message = "";

		if (null == cr) {
			message = "Error in getting data of cost estimation";
		}
		return ResultDtoFactory.toAck(message, cr);
	}
	
	@RequestMapping(value = "/costExcel", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView exportCostExcel(@RequestBody CostParameters parameters) {
		CostEstimationResult data = costEstimationService.getCostEstimationResult(parameters);
		Map<String, CostEstimationResult> model = new HashMap<String, CostEstimationResult>();
		model.put("data", data);
		CostExcelView view = new CostExcelView();
		return new ModelAndView(view, model);
	}
	
}
