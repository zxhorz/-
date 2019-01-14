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

import com.hengtiansoft.bluemorpho.workbench.domain.JobStatus;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.enums.AnalysisType;
import com.hengtiansoft.bluemorpho.workbench.enums.JobProcessStatus;
import com.hengtiansoft.bluemorpho.workbench.repository.AnalysisTypeRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.JobStatusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;

@Api(tags = {"SystemOntology"}, description = "the system ontology API")
@Controller
@RequestMapping(value = "/so")
public class SystemOntologyController extends AbstractController{

	private static final Logger LOGGER = Logger.getLogger(SystemOntologyController.class);
	@Autowired
	JobStatusRepository jobStatusRepository;
	
	@Autowired
	private AnalysisTypeRepository analysisTypeRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@ApiOperation(value = "Get the status of System Ontology with project ID",
			nickname = "getStatus",
			notes = "returns the status of system ontology in specific project. ")
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "is not generated"),
        @ApiResponse(code = 200, message = "is being generated from"),
        @ApiResponse(code = 200, message = "is generated on")
        })
	@RequestMapping(value = "/status", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> getStatus(@RequestParam("projectId") String projectId) {

		String analysisTypeId = analysisTypeRepository.findIdByName(AnalysisType.SO.toString());
		JobStatus job = jobStatusRepository.findByProjectIdAndAnalysisTypeId(projectId, analysisTypeId);
		String message;
		if(null==job){
			message = "is not generated";
			return ResultDtoFactory.toAck("NS",message);
		}else{
			if(JobProcessStatus.S.toString().equals(job.getStatus())){
				message = "is generated on " + job.getStopTime();
				return ResultDtoFactory.toAck(job.getStatus(),message);
			}else if(JobProcessStatus.P.toString().equals(job.getStatus())){
				message = "is being generated from " + job.getStartTime();	
				return ResultDtoFactory.toAck(job.getStatus(),message);
			}else{
				message = "is not generated ";	
				return ResultDtoFactory.toAck("NS",message);				
			}			
		}
	}
	
}
