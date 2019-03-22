package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 6, 2018 10:59:26 AM
 */
@Component
public class CodeVersionUtil {

	@Autowired
	private ProjectRepository projectRepository;
	
	/**
	 * find the the last modified time(simplify 
	 * the code version) in cobol and copybook directory.
	 */
	public String getCodeVerionInFs(String projectId) {
		Project project = projectRepository.findOne(projectId);
		String projectPath = project.getPath().replace("\\", "/");
		String cobolPath = projectPath + "/cobol";
		String copybookPath = projectPath + "/copybook";
		File cobolDir = new File(cobolPath);
		File copybookDir = new File(copybookPath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		long cobolLastModified = cobolDir.lastModified();
		long cpyLastModified = copybookDir.lastModified();
		long latest = cobolLastModified > cpyLastModified ? cobolLastModified : cpyLastModified;
		cal.setTimeInMillis(latest);
		return sdf.format(cal.getTime());
	}

	public Date getCodeVerionDateInFs(String projectId) {
		Project project = projectRepository.findOne(projectId);
		String projectPath = project.getPath().replace("\\", "/");
		String cobolPath = projectPath + "/cobol";
		String copybookPath = projectPath + "/copybook";
		File cobolDir = new File(cobolPath);
		File copybookDir = new File(copybookPath);
		Calendar cal = Calendar.getInstance();
		long cobolLastModified = cobolDir.lastModified();
		long cpyLastModified = copybookDir.lastModified();
		long latest = cobolLastModified > cpyLastModified ? cobolLastModified : cpyLastModified;
		cal.setTimeInMillis(latest);
		return cal.getTime();
	}
	
}
