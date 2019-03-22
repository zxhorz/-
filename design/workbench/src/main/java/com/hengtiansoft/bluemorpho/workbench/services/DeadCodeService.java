package com.hengtiansoft.bluemorpho.workbench.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.DeadCodeResult;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 5, 2018
 */
@Service
public class DeadCodeService {

	@Autowired
	ProjectRepository projectRepository;

	public DeadCodeResult getDeadCodeSummary(int projectId) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		String projectPath = project.getPath();
		// TODO
		return new DeadCodeResult();
	}

}
