package com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 7:35:43 PM
 */
public class OperationAndDependency {

	// 界面点击操作本身的analysis id
	private String analysisId;
	// 分析针对的项目id
	private String projectId;
	// 做此分析的用户id
	private String userId;
	// 用户点击该分析时间
	private String time;
	// 单次前台job请求中，该分析是否为其他分析的依赖项(若是，则重复)
	private boolean duplicated = false;
	// 该分析操作的依赖(简化分level层级)
	private Stack<DependencyLevel> stack = new Stack<DependencyLevel>();

	public OperationAndDependency() {
	}
	
	public OperationAndDependency(String analysisId, String projectId, String userId) {
		this.analysisId = analysisId;
		this.projectId = projectId;
		this.userId = userId;
		SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		this.time = simpleDateFormatter.format(new Date());
		offerLevel();
		addDependencyId(analysisId);
	}
	
	public void offerLevel() {
		DependencyLevel dependencyLevel = new DependencyLevel();
		this.stack.push(dependencyLevel);
	}

	public void addDependencyId(String analysisId) {
		DependencyLevel peek = this.stack.peek();
		peek.add(analysisId);
	}
	
	public void addDependencyIds(List<String> analysisIds) {
		DependencyLevel peek = this.stack.peek();
		peek.addAll(analysisIds);
	}

	public List<String> getAllDependencies() {
		List<String> result = new ArrayList<String>();
		for (int i= 0; i < this.stack.size(); i++) {
			List<String> ids = this.stack.get(i).getAnalysisTypeId();
			result.addAll(ids);
		}
		return result;
	}
	
	public Stack<DependencyLevel> getStack() {
		return stack;
	}

	public void setStack(Stack<DependencyLevel> stack) {
		this.stack = stack;
	}

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isDuplicated() {
		return duplicated;
	}

	public void setDuplicated(boolean duplicated) {
		this.duplicated = duplicated;
	}
	
}
