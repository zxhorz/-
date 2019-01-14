package com.hengtiansoft.bluemorpho.workbench.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.AutoTagFeedback;
import com.hengtiansoft.bluemorpho.workbench.dto.AutoTagRequest;
import com.hengtiansoft.bluemorpho.workbench.dto.AutoTagResult;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;

/**
 * @Description: auto tag
 * @author gaochaodeng
 * @date Aug 21, 2018
 */
@Service
public class AutoTagService {
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	PortUtil portUtil;
	private static final Logger LOGGER = Logger
			.getLogger(CostEstimationService.class);

	public List<AutoTagResult> getAutoTagResults(String filePath) {
		LOGGER.info("Get auto tags");
		List<AutoTagResult> results = new ArrayList<AutoTagResult>();
		results = FilePathUtil.readJson(filePath, AutoTagResult.class);
		return results;
	}

	public AutoTagRequest prepareAutoTagRequest(String projectId,
			List<String> names, String type) {
		AutoTagRequest request = new AutoTagRequest();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return request;
		}
		request.setProjectId(projectId);
		request.setOutputPath(FilePathUtil.getTagResultPath());
		// TODO 目前这三个参数还不明确使用场景，暂时给一个test值
		request.setOrganization("test");
		request.setBusinesDomain("test");
		request.setSystem("test");
		// set params
		List<AutoTagResult> params = new ArrayList<AutoTagResult>();
		for (String name : names) {
			AutoTagResult result = new AutoTagResult(name, type);
			params.add(result);
		}
		request.setParams(params);
		return request;
	}

	/**
	 * 选中一个文件，打tag，反馈
	 *
	 * @param projectId
	 * @param name
	 * @param type
	 * @param tag
	 */
	public void feedback(String projectId, String name, String type, String tag) {
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return;
		}
		// mapping type
		type = FileStatusUtil.autoTagTypeMapping(type);
		// 方便传参，tag为空格相连的字符串，现在拆分
		List<String> tags = Arrays.asList(tag.split(" "));
		AutoTagResult tagResult = new AutoTagResult(name, type, tags);
		// 读取feedback.json
		String feedbackPath = FilePathUtil.getAutoFeedbackPath();
		List<AutoTagFeedback> feedbacks = FilePathUtil.readJson(feedbackPath,
				AutoTagFeedback.class);
		// feedbacks中查找是否存在该projectId记录
		AutoTagFeedback existFeedback = checkProject(projectId, feedbacks);
		if (existFeedback != null) {
			// 存在，则判断是否已经有该程序的tag更新记录。有，则替换tags；否则新增一条AutoTagResult记录
			AutoTagResult item = checkAutoTagResult(
					existFeedback.getUpdateTags(), name, type);
			if (item != null) {
				item.setTags(tags);
			} else {
				existFeedback.getUpdateTags().add(tagResult);
			}
		} else {
			// 不存在，则直接追加至feedbacks中
			// TODO 目前organization, businessDomain, system参数还不明确使用场景，暂时给一个test值
			existFeedback = new AutoTagFeedback(projectId, "test", "test",
					"test", project.getPath(), FileStatusUtil.checkCode(project
							.getPath()));
			existFeedback.getUpdateTags().add(tagResult);
			feedbacks.add(existFeedback);
		}
		// 将feedback写回至json文件中
		FilePathUtil.writeJson(feedbackPath, feedbacks, AutoTagFeedback.class);
	}

	private AutoTagFeedback checkProject(String projectId,
			List<AutoTagFeedback> feedbacks) {
		for (AutoTagFeedback autoTagFeedback : feedbacks) {
			if (autoTagFeedback.getProjectId().equals(projectId)) {
				return autoTagFeedback;
			}
		}
		return null;
	}

	private AutoTagResult checkAutoTagResult(List<AutoTagResult> updateTags,
			String name, String type) {
		for (AutoTagResult item : updateTags) {
			if (item.getName().equalsIgnoreCase(name)
					&& item.getType().equalsIgnoreCase(type)) {
				return item;
			}
		}
		return null;
	}
}
