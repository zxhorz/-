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
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;

@Api(tags = {"PyServer"}, description = "api for py server")
@Controller
@RequestMapping(value = "/pyserver")
public class PyServerDemoController extends AbstractController{
	private static final Logger logger = Logger.getLogger(JobController.class);
	
	@Autowired
	ProjectRepository projectRepository;
	
	@ApiOperation(value = "test demo for py server",
			nickname = "py server test",
			notes = "api for py server, test dmo")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK")
            })
	@RequestMapping(value = "/demotest", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<Project> demotest() {
		Project project = projectRepository.findOne("12") ;
		return ResultDtoFactory.toAck("",project);
	}
}
