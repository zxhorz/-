package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 29, 2018
 */
public class CostEstimationResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<CostProgramResult> programResults = new ArrayList<CostProgramResult>();
	private List<CostCloneResult> cloneResults = new ArrayList<CostCloneResult>();
	private double programSubTotal;
	private double cloneSubTotal;
	private double grandTotal;
	private double withBudget;
	private CostParameters costParameters;

	public List<CostProgramResult> getProgramResults() {
		return programResults;
	}

	public void setProgramResults(List<CostProgramResult> programResults) {
		this.programResults = programResults;
	}

	public List<CostCloneResult> getCloneResults() {
		return cloneResults;
	}

	public void setCloneResults(List<CostCloneResult> cloneResults) {
		this.cloneResults = cloneResults;
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public double getWithBudget() {
		return withBudget;
	}

	public void setWithBudget(double withBudget) {
		this.withBudget = withBudget;
	}

	public double getProgramSubTotal() {
		return programSubTotal;
	}

	public void setProgramSubTotal(double programSubTotal) {
		this.programSubTotal = programSubTotal;
	}

	public double getCloneSubTotal() {
		return cloneSubTotal;
	}

	public void setCloneSubTotal(double cloneSubTotal) {
		this.cloneSubTotal = cloneSubTotal;
	}

	public CostParameters getCostParameters() {
		return costParameters;
	}

	public void setCostParameters(CostParameters costParameters) {
		this.costParameters = costParameters;
	}

}
