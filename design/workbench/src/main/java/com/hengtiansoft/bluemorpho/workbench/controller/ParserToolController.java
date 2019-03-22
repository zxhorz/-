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

import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.ParserToolService;

@Api(tags = {"ParserTool"}, description = "the patsert tools API")
@Controller
@RequestMapping(value = "/parser")
public class ParserToolController extends AbstractController{
	private static final Logger LOGGER = Logger.getLogger(ParserToolController.class);
	
	@Autowired 
	ParserToolService parserToolService;
	
	@ApiOperation(value = "get the parser tree of specific source code",
			nickname = "getParserTree",
			notes = "get the parser tree of specific source code, and print in console")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in generating") })
	@RequestMapping(value = "/getParserTree", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<String> getParserTree(@RequestParam("preprocessedCode") String preprocessedCode) {
			LOGGER.info(preprocessedCode);
			String context = parserToolService.getParserTree(preprocessedCode);
			return ResultDtoFactory.toAck("",context);
	}
}
