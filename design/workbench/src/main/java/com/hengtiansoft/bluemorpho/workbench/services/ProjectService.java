package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.domain.UserRoleInProject;
import com.hengtiansoft.bluemorpho.workbench.dto.PreparedSearchInfoResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ProjectInfo;
import com.hengtiansoft.bluemorpho.workbench.repository.CustomScriptRunHistoryRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.JobStatusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRoleInProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.OperationLogger;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.UserUtil;

@Service
public class ProjectService {
    private static final Logger LOGGER = Logger.getLogger(ProjectService.class);
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserRoleInProjectRepository userRoleInProjectRepository;
	@Autowired
	PortUtil portUtil;
	@Autowired
	CustomScriptRunHistoryRepository customScriptRunHistoryRepository;
	@Autowired
	JobStatusRepository jobStatusRepository;
	@Autowired
	UserUtil userUtil;
	
	@Autowired
	OperationLogger operationLogger;

	public List<Project> getProjectList() {
		List<Project> projectList = new ArrayList<Project>();
		String userId = userUtil.getCurrentUserId();
		List<UserRoleInProject> urips = userRoleInProjectRepository
				.findAllByUserId(userId);
		for (UserRoleInProject urip : urips) {
			if (urip.getProjectId().equals("all")) {
				projectList = new ArrayList<Project>();
				Iterator<Project> it = projectRepository.findAll().iterator();
				while (it.hasNext()) {
					projectList.add(it.next());
				}
			} else {
				projectList.add(projectRepository.findOne(urip.getProjectId()));
			}
		}			

		return projectList;
	}
	
	public Project createNewProject(ProjectInfo projectInfo) {

		// find all project info by signin id
		String userId = userUtil.getCurrentUserId();
		Project project = new Project();
		project.setName(projectInfo.getProjectName());
		project.setDescription(projectInfo.getDescription());
		project.setCreaterId(userId);
		project.setCreatedTime(new Date());
		//TODO
		project.setFileMapping("DEFAULT");
		project.setPath(FilePathUtil.createDefaultPath(projectInfo
				.getProjectName()));
		project = projectRepository.save(project);
		operationLogger.saveCreateOperation(project.getId(),project.getName());
		UserRoleInProject ur = new UserRoleInProject();
		ur.setProjectId(project.getId());
		//默认使用1，后续区分角色权限后，此处需要修改
		ur.setRoleId("1");
		ur.setUserId(userId);
		userRoleInProjectRepository.save(ur);
		return project;
	}

	public PreparedSearchInfoResponse prepareSearchInfo(String projectId) {
		//封装codesearch需要的查询参数
		Project project = projectRepository.findOne(projectId);
		if(null==project){
			return null;
		}
		 
		PreparedSearchInfoResponse info = new PreparedSearchInfoResponse();
		String outputPath = FilePathUtil.getSearchOutputPath(project.getPath());
		//code version stored in output/search
		String searchCodeVersion = FileStatusUtil.getSearchVersion(outputPath);
		//code version stored in config/
		String codeVersion = FileStatusUtil.getSearchVersion(FilePathUtil.getPath(project.getPath(),"CONFIG"));
		String dbUrl = portUtil.getBoltUrl(projectId);
		String sourcePath = FilePathUtil.getPath(project.getPath(), "SOURCE");
		info.setRootPath(sourcePath);
		info.setDburl(dbUrl);
		//目前只支持在COBOL文件中查找，后续如果实现在COBOL以外问价查找或者COBOL文件的存储路径变化（默认COBOL代码存放在COBOL文件夹下面），
		//需要修改sourcePath的内容，并同步需要改修改注释内容。
		sourcePath = FilePathUtil.getPath(sourcePath, "COBOL");
		info.setProjectPath(sourcePath);
		if("0".equals(searchCodeVersion)){
			FileStatusUtil.wirteNewSearchVersion(project.getPath(),outputPath,Integer.valueOf(codeVersion).intValue());
			info.setFlag("Y");
			info.setOutputPath(FilePathUtil.getSearchResultPath(outputPath));
			info.setType("program");
		}else{
			if(Integer.valueOf(searchCodeVersion).intValue()==Integer.valueOf(codeVersion).intValue()){
				info.setFlag("N");
				info.setOutputPath(FilePathUtil.getSearchResultPath(outputPath));	
				info.setType("program");
			}else{
				if(Integer.valueOf(searchCodeVersion).intValue()<Integer.valueOf(codeVersion).intValue()){
					Map<String,List<String>> diff = FileStatusUtil.diffFileList(project.getPath(), outputPath + "/filestatus.txt");
					info.setFlag("N");
					info.setOutputPath(FilePathUtil.getSearchResultPath(outputPath));
					info.setAddFiles(diff.get("ADD"));
					info.setDeleteFiles(diff.get("DELETE"));
					info.setType("program");	
					FileStatusUtil.wirteNewSearchVersion(project.getPath(),outputPath,Integer.valueOf(codeVersion).intValue());
				}else{
					info.setFlag("N");
					info.setOutputPath(FilePathUtil.getSearchResultPath(outputPath));
					info.setType("program");					
				}
			}
		}
		return info;
	}

    public int delete(Project project) {
        try {
            String path = project.getPath();
            String projectId = project.getId();
            customScriptRunHistoryRepository.deleteByProjectId(projectId);
            jobStatusRepository.deleteByProjectId(projectId);
            userRoleInProjectRepository.deleteByProjectId(projectId);
            projectRepository.delete(project);
            FileUtils.forceDelete(new File(path));
            return 0;
        } catch (Exception e) {
            LOGGER.error(e);
            return -1;
        }
    }

}
