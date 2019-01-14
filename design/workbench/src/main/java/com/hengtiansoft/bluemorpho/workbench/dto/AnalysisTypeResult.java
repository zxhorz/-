package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.ArrayList;
import java.util.List;

import com.hengtiansoft.bluemorpho.workbench.domain.AnalysisType;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 7, 2018
 */
public class AnalysisTypeResult {
	private List<AnalysisType> analysisTypes = new ArrayList<AnalysisType>();
	private List<JobDependency> analysisDependencies = new ArrayList<JobDependency>();

	public AnalysisTypeResult() {
		super();
	}

	public AnalysisTypeResult(List<AnalysisType> analysisTypes,
			List<JobDependency> analysisDependencies) {
		super();
		this.analysisTypes = analysisTypes;
		this.analysisDependencies = analysisDependencies;
	}

	public List<AnalysisType> getAnalysisTypes() {
		return analysisTypes;
	}

	public void setAnalysisTypes(List<AnalysisType> analysisTypes) {
		this.analysisTypes = analysisTypes;
	}

	public List<JobDependency> getAnalysisDependencies() {
		return analysisDependencies;
	}

	public void setAnalysisDependencies(
			List<JobDependency> analysisDependencies) {
		this.analysisDependencies = analysisDependencies;
	}

}
