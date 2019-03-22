package com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 7:45:09 PM
 */
public class DependencyLevel {

	private List<String> analysisTypeId = new ArrayList<String>();

	public void add(String id) {
		if (!this.analysisTypeId.contains(id)) {
			this.analysisTypeId.add(id);
		}
	}
	
	public void addAll(List<String> ids) {
		for (String id : ids) {
			if (!this.analysisTypeId.contains(id)) {
				this.analysisTypeId.addAll(ids);
			}
		}
	}
	
	public List<String> getAnalysisTypeId() {
		return analysisTypeId;
	}

	public void setAnalysisTypeId(List<String> analysisTypeId) {
		this.analysisTypeId = analysisTypeId;
	}
	
}
