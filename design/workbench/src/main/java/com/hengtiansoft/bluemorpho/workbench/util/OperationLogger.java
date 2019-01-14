package com.hengtiansoft.bluemorpho.workbench.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.OperationLog;
import com.hengtiansoft.bluemorpho.workbench.enums.AnalysisType;
import com.hengtiansoft.bluemorpho.workbench.enums.OperationType;
import com.hengtiansoft.bluemorpho.workbench.repository.OperationLogRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRoleInProjectRepository;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 15, 2018 1:18:42 PM
 */
@Component
public class OperationLogger {

	@Autowired
	private OperationLogRepository operationLogRepository;
	
	@Autowired
	UserRoleInProjectRepository userRoleInProjectRepository;
	
	@Autowired
	UserUtil userUtil;
	
	/**
	 * 记录project-admin更改他人权限的操作
	 */
	public void savePrivilegeChange(String manipulatorUserId, 
			String manipulatedUserId, String projectId, String roleId, String detail) {
		OperationLog op = new OperationLog(manipulatorUserId, manipulatedUserId,
				projectId, roleId, OperationType.CHANGE_PRIVILEGE.toString(),
				detail, getCurrentTime());
		operationLogRepository.save(op);
	}
	
	/**
	 * 记录登录操作
	 */
	public void saveLogin(String manipulatorUserId, String detail) {
		OperationLog op = new OperationLog(manipulatorUserId, OperationType.LOGIN.toString(), detail, getCurrentTime());
		operationLogRepository.save(op);
	}
	
	/**
	 * 记录登出操作
	 */
	public void saveLogout(String manipulatorUserId, String detail) {
		OperationLog op = new OperationLog(manipulatorUserId, OperationType.LOGOUT.toString(), detail, getCurrentTime());
		operationLogRepository.save(op);
	}
	
	/**
	 * 记录新建project操作
	 */
	public void saveCreateOperation(String projectId, String projectName) {
		String userId = userUtil.getCurrentUserId();
		String roleId = userUtil.getCurrentUserRole(projectId);
		String time = getCurrentTime();
		String detail = "Create new project " + projectName + " at " + time;
		OperationLog op = new OperationLog(userId, projectId, roleId, 
				OperationType.CREATE_PROJECT.toString(), detail, time);
		operationLogRepository.save(op);
	}
	
	/**
	 * 记录操作记录
	 */
	private void saveAnalyseRecord(String projectId,String projectName,AnalysisType job, String jobId, Date startTime ,boolean isSo) {
		String userId = userUtil.getCurrentUserId();
		String roleId = userUtil.getCurrentUserRole(projectId);
		String time = getCurrentTime(startTime);
		String detail;
		if(isSo){
			detail = "Generate the System Ontology of " + projectName + " at " + time;
		}else{
			detail = "Do the " + job.toString() + " on " + projectName + " at " + time;
		}
		OperationLog op = new OperationLog(userId, projectId, roleId, 
				job.toString(), detail, getCurrentTime());
		operationLogRepository.save(op);
	}
	
	/**
	 * 记录生成System Ontology操作
	 */
	public void saveGenerateSoOperation(String projectId, String projectName, String jobId, Date startTime) {
		saveAnalyseRecord(projectId,projectName,AnalysisType.SO,jobId,startTime, true);
	}
	
	/**
	 * 记录分析project分析操作
	 */
	public void saveAnalyse(String projectId,String projectName,AnalysisType job, String jobId,Date startTime){
		saveAnalyseRecord(projectId,projectName,job,jobId, startTime, false);
	}
	
	public void saveOperation(String manipulatorUserId,
			String manipulatedUserId, String projectId, String roleId,
			String operationType, String detail) {
		OperationLog op = new OperationLog(manipulatorUserId, manipulatedUserId, 
				projectId, roleId, operationType, detail, getCurrentTime());
		operationLogRepository.save(op);
	}
	
	public static String getCurrentTime() {
		return getCurrentTime(new Date());
	}
	
	private static String getCurrentTime(Date startTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(startTime);
	}
}

