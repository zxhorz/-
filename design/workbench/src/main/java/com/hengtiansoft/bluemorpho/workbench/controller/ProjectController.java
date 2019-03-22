package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.hengtiansoft.bluemorpho.workbench.constant.ResultCode;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.PreparedSearchInfoResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ProjectInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.ProjectListResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServerPool;
import com.hengtiansoft.bluemorpho.workbench.quartz.QuartzManager;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRoleInProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.services.ProjectService;
import com.hengtiansoft.bluemorpho.workbench.util.FileMappingUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.OperationLogger;
import com.hengtiansoft.bluemorpho.workbench.util.UserUtil;

@Api(tags = {"Project"}, description = "the project API")
@Controller
@RequestMapping(value = "/project")
public class ProjectController extends AbstractController {
	private static final Logger LOGGER = Logger.getLogger(ProjectController.class);
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired 
	QuartzManager quartzManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRoleInProjectRepository userRoleInProjectRepository;

	@Autowired
	Neo4jServerPool pool;	
	
	@Autowired
	OperationLogger operationLogger;
	
	@Autowired
	ProjectService projectService;
		
	@Autowired
	UserUtil userUtil;

	@Autowired
	FileMappingUtil fileMappingUtil;
	/**
	 * Description: render the detail page of a product
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "Get list of all projects",
			nickname = "projectList",
			notes = "returns the list of projects the user created on the workbench. ")
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "An array of projects"),
        @ApiResponse(code = 200, message = "Unexpected error", response = Error.class) })
    @RequestMapping(value = "/list", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ProjectListResponse projectList() {

		List<Project> projectList = projectService.getProjectList();
		return new ProjectListResponse().code(ResultCode.ACK).message(StringUtils.EMPTY).data(projectList);
	}

	@ApiOperation(value = "Create new project",
			nickname = "createNewProject",
			notes = "Create a new project with specified name and description, under the name of the login user")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/add",	method = RequestMethod.POST,
    		produces = { "application/json" },
    		consumes = { "application/json" })
	@ResponseBody
	public ResultDto<Project> createNewProject(
			@RequestBody @Validated ProjectInfo projectInfo,
			BindingResult result) {
		if (result.hasErrors()) {
			return ResultDtoFactory.toAck(buildErrorMessage(result));
		}
		
		Project oldProject = projectRepository.findByProjectName(projectInfo
				.getProjectName());

		if (null != oldProject) {
			return ResultDtoFactory.toNack("Name is already taken");
		}
		
		Project project = projectService.createNewProject(projectInfo);
		return ResultDtoFactory.toAck("Successful", project);
	}

	@ApiOperation(value = "Update project",
			nickname = "updateProject",
			notes = "Update a project's name and description")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/updateproject", method = RequestMethod.POST,
			produces = { "application/json" },
			consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> updateProject(
			@RequestBody @Validated ProjectInfo projectInfo,
			BindingResult result) {
		if (result.hasErrors()) {
			return ResultDtoFactory.toAck(buildErrorMessage(result));
		}
		Project oldProject = projectRepository.findOne(projectInfo.getProjectId());
		
		if(null==oldProject){
			return ResultDtoFactory.toNack("This project is not existed");
		}
		
		oldProject.setDescription(projectInfo.getDescription());
		try{
			projectRepository.save(oldProject);
			return ResultDtoFactory.toAck("Successful", "Update Successful");			
		}catch(Exception e){
			return ResultDtoFactory.toNack("Error");			
		}

	}

	@ApiOperation(value = "open neo4j DB resouce",
			nickname = "openProject",
			notes = "Open the neo4j DB resource of the specific project")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/open", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<String> openProject(@RequestParam("projectId") String projectId) {
//		 start neo4j server
		Project project = projectRepository.findOne(projectId);
		if (project != null) {
			String codeVersion = FileStatusUtil.checkCode(project.getPath());
			String databasePath =  FilePathUtil.getNeo4jPath(project.getPath(), codeVersion); 
			File db = new File(databasePath);
			if (!db.exists()) {
				return ResultDtoFactory.toAck("F", "No neo4j path existed");
			} else {
				pool.getAndCreateDB(project.getPath(), codeVersion);
				LOGGER.info("Neo4j DB is opened");
				return ResultDtoFactory.toAck("S", "Neo4j DB is opened");
			}
		} else {
			return ResultDtoFactory.toAck("F", "Project is not existed");
		}
	}
	
    @ApiOperation(value = "delete project", nickname = "deleteProject", notes = "delete the project and its info")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in deleteing") })
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> deleteProject(@RequestParam("projectIds") String[] projectIds) {
        for (String projectId : projectIds) {
            int code;
            Project project = projectRepository.findOne(projectId);
            if (project != null) {
                // 检查job
                if (quartzManager.checkProjectHasJob(projectId))
                    return ResultDtoFactory.toAck("F", "Project " + project.getName() + " is analyzing");
                LOGGER.info("Finished checking Project " + project.getName() + "'s job");
                // 关闭neo4j

                String codeVersion = FileStatusUtil.checkCode(project.getPath());
                String databasePath = FilePathUtil.getNeo4jPath(project.getPath(), codeVersion);
                File db = new File(databasePath);
                if (!db.exists()) {
                    code = 0;
                } else {
                    code = pool.destroyWithReturn(project.getPath() + "/" + codeVersion);
                }
                LOGGER.info("Finished closing neo4j");
            } else {
                return ResultDtoFactory.toAck("F", "Project of Id " + projectId + " is not existed");
            }
            if (code == 0) {
                int result = projectService.delete(project);   
                if(result < -1) {
                	return ResultDtoFactory.toAck("F", "Project " + project.getName() + " failed to delete");
                }
            }
            else {
                return ResultDtoFactory.toAck("F", "Project " + project.getName() + " failed to close neo4j");         	
            }

        }
        LOGGER.info("Finished deleting projects");
        return ResultDtoFactory.toAck("S", "Project deleted successfully");
    }
	

	@ApiOperation(value = "check code version of the specific project, and prepare the search info",
			nickname = "checkCodeVersion",
			notes = "check code version of the specific project, and prepare the search info")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in opening") })
	@RequestMapping(value = "/codeversion", method = RequestMethod.GET)
	@ResponseBody
	public ResultDto<PreparedSearchInfoResponse> checkCodeVersion(@RequestParam("projectId") String projectId) {
		PreparedSearchInfoResponse info = projectService.prepareSearchInfo(projectId);
		return ResultDtoFactory.toAck("", info);
	}
	
    @ApiOperation(value = "uploadfiles")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in uploading") })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> uploadProject(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                .getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(request)) {
            
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Iterator iter = multiRequest.getFileNames();

            while (iter.hasNext()) {
                // 一次遍历所有文件
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    String path = file.getOriginalFilename();
                    // 上传
                    try {
                        file.transferTo(new File(path));
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        return ResultDtoFactory.toAck("fail");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        return ResultDtoFactory.toAck("fail");
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("上传时间：" + String.valueOf(endTime - startTime) + "ms");
        return ResultDtoFactory.toAck("success");
    }   
	
	private String buildErrorMessage(BindingResult result) {
		List<FieldError> err = result.getFieldErrors();
		FieldError fe;
		String errorMessage = "";
		for (int i = 0; i < err.size(); i++) {
			fe = err.get(i);
			String msg = fe.getDefaultMessage();
			errorMessage = errorMessage + msg + "/n";
		}
		return errorMessage;
	}
}
