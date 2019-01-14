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

import com.hengtiansoft.bluemorpho.workbench.dto.AnalysisTypeResult;
import com.hengtiansoft.bluemorpho.workbench.dto.DependencyInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.JobStatusResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgressBarResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ProjectStatusResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.enums.JobRequestHandleType;
import com.hengtiansoft.bluemorpho.workbench.quartz.QuartzManager;
import com.hengtiansoft.bluemorpho.workbench.services.JobService;

@Api(tags = {"Job"}, description = "the job manager API")
@Controller
@RequestMapping(value = "/job")
public class JobController extends AbstractController{
	private static final Logger LOGGER = Logger.getLogger(JobController.class);
		
	@Autowired
	JobService jobService;
	@Autowired
	QuartzManager quartzManager;

	@ApiOperation(value = "check the status of job and source code",
			nickname = "checkstatus",
			notes = "Check current status of job and source code,return ONDATE or OUTOFDATE or ONDOING")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK")
            })
	@RequestMapping(value = "/checkstatus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> checkStatus(@RequestParam("projectId") int projectId) {
//		 start neo4j server
		String status = jobService.checkStatus(String.valueOf(projectId));
		if("NPE".equals(status)){
			return ResultDtoFactory.toNack("The project is not existed", status);			
		}
		return ResultDtoFactory.toAck("", status);
            }

	@ApiOperation(value = "get all analysis types and dependencies", nickname = "getAnalysisTypeAndDependency", notes = "Get all analysis types and dependencies, return AnalysisTypeResult")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/getAllType", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<AnalysisTypeResult> getAllAnalysisDependency() {
		AnalysisTypeResult result = jobService.getAllAnalysisDependency();
		return ResultDtoFactory.toAck("", result);
	}
	
	@ApiOperation(value = "save selected analysis types into specific file", nickname = "saveSelectedAnalysisType", notes = "Save selected analysis types into specific file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/saveSelectedType", method = RequestMethod.POST,    		
	produces = { "application/json" },
    consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> saveSelectedAnalysisType(@RequestBody DependencyInfo dependencyInfo) {
		String flag = jobService.saveSelectedAnalysisType(dependencyInfo);
		return ResultDtoFactory.toAck("Succeed to save selected analysis type",
				flag);
	}

	@ApiOperation(value = "update selected analysis types into specific file", nickname = "updateSelectedAnalysisType", notes = "update selected analysis types into specific file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/updateSelectedType", method = RequestMethod.POST,    		
	produces = { "application/json" },
    consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> updateSelectedAnalysisType(@RequestBody DependencyInfo dependencyInfo) {
		jobService.updateSelectedAnalysisType(dependencyInfo);
		return ResultDtoFactory.toAck("",
				"Succeed to save selected analysis type.");
	}
	
	@ApiOperation(value = "get selected analysis types from specific file", nickname = "getSelectedAnalysisType", notes = "Get selected analysis types from specific file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/getSelectedType", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<String>> getSelectedAnalysisType(int projectId) {
		List<String> typeIds = jobService.getSelectedAnalysisType(String.valueOf(projectId));
		return ResultDtoFactory.toAck("", typeIds);
	}
	
	@ApiOperation(value = "exec the selected jobs", nickname = "execJobs", notes = "Execute the specific jobs")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/execjob", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<String>> execJobs(int projectId) {
		// 先判断项目下是否有COBOL文件
        if (jobService.getFileCount(projectId, "COBOL") == 0) {
            return ResultDtoFactory.toNack("No file in source code");
        }
        List<String> analysisTypeIds = jobService.getSelectedAnalysisType(String.valueOf(projectId));
        JobRequestHandleType type = quartzManager.handleAnalysisRequest(String.valueOf(projectId), analysisTypeIds);
        String msg = "";
        switch (type) {
        case WAITING_TO_RUN:
            msg = "current analysis is waiting to run.";
            break;
        case HAS_REPORTS:
            msg = "current analysis has reports.";
            break;
        case CONTAINED_IN_PRE_ANALYSIS:
            msg = "current analysis is contained in pre job.";
            break;
        case ERROR:
            msg = "error.";
            break;
        }
        return ResultDtoFactory.toAck(msg);
	}
	
	@ApiOperation(value = "show job with update status", nickname = "jobUpdateStatus", notes = "List all job status, figure out whether the job need to be updated")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/jobUpdateStatus", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<JobStatusResponse>> jobUpdateStatus(int projectId) {
		List<JobStatusResponse> responses = jobService
				.getJobUpdateStatus(String.valueOf(projectId));
		return ResultDtoFactory.toAck("S", responses);
	}

	@ApiOperation(value = "get all job status under the project", nickname = "getAllJobStatus", notes = "Get all job status under the project")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/jobProgressBarStatus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<ProgressBarResponse>> getJobProgressBarStatus(int projectId) {
		List<ProgressBarResponse> pbs = jobService.getJobProgressBarStatus(String.valueOf(projectId));
		return ResultDtoFactory.toAck("", pbs);
	}

	@RequestMapping(value = "/jobHistory", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<JobStatusResponse>> getJobHistory(int projectId) {
		List<JobStatusResponse> jbs = jobService.getJobHistory(String.valueOf(projectId));
		return ResultDtoFactory.toAck("",jbs);
	}

	@ApiOperation(value = "get job start time", nickname = "getJobStartTime", notes = "Get job start time")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/startTime", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> getJobStartTime(String jobName) {
		String startTime = jobService.getJobStartTime(jobName);
		return ResultDtoFactory.toAck("", startTime);
	}
	
	@ApiOperation(value = "get the specific job status under the project", nickname = "getSpecJobStatus", notes = "Get the spicific job status under the project")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/specjobstatus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> getSpecJobStatus(int projectId,String jobName) {
		String flag = jobService.getSpecJobStatus(String.valueOf(projectId),jobName);
		return ResultDtoFactory.toAck("",flag);
	}
	
	@ApiOperation(value = "get the specific job dependency", nickname = "getJobDependency", notes = "Get the spicific job dependency")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/jobdependency", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<JobStatusResponse>> getJobDependency(int projectId,String jobName) {
		List<JobStatusResponse> jobs = jobService.getJobDependency(String.valueOf(projectId),jobName);
		return ResultDtoFactory.toAck("",jobs);
	}
	
	@ApiOperation(value = "exec the specific jobs now", nickname = "generateJob", notes = "exec the specific jobs now")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/generateJob", method = RequestMethod.POST,    		
	produces = { "application/json" },
    consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> generateJob(@RequestBody DependencyInfo dependencyInfo) {
		//借用DependencyInfo的selectedName存储id
		List<String> analysisTypeIds = dependencyInfo.getSelectedName();
		JobRequestHandleType type = quartzManager.handleAnalysisRequest(dependencyInfo.getProjectId(), analysisTypeIds);
		String msg = "";
		switch(type) {
		case WAITING_TO_RUN:
			msg = "current analysis is waiting to run.";
			break;
		case HAS_REPORTS:
			msg = "current analysis has reports.";
			break;
		case CONTAINED_IN_PRE_ANALYSIS:
			msg = "current analysis is contained in pre job.";
			break;
		case ERROR:
			msg = "error.";
			break;
		}
		return ResultDtoFactory.toAck(msg);
	}
	
	@ApiOperation(value = "get the status list of job and source code",
			nickname = "getProjectStatus",
			notes = "check current status of jobs and source code ,return status list")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK")
            })
	@RequestMapping(value = "/projectstatus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<ProjectStatusResponse> getPorjectStatus(@RequestParam("projectId") int projectId) {
		ProjectStatusResponse psr = new ProjectStatusResponse();
		psr = jobService.getProjectStatus(String.valueOf(projectId));
		return ResultDtoFactory.toAck("", psr);
	}

	@ApiOperation(value = "check if has any cobol file in source code path",
			nickname = "checkFileCount",
			notes = "check if has any cobol file in source code path")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
            })
	@RequestMapping(value = "/checkFileCount", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> checkFileCount(@RequestParam("projectId") int projectId) {
		int count = jobService.getFileCount(projectId);
		return ResultDtoFactory.toAck("", String.valueOf(count));
	}
}
