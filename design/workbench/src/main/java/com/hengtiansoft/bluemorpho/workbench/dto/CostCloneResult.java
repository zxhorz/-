package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Description: cost estimation 中的group信息
 * @author gaochaodeng
 * @date Jun 28, 2018
 */
public class CostCloneResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private int groupNo;
	private HashMap<String, Integer> paraTierMap = new HashMap<String, Integer>();
	private double costPoint;
	private double totalCostPoint;
	private double manHour;
	private int paraCount;
	private double combinedOutput;
	private int tier_1 = 0;

	public CostCloneResult() {
		super();
	}

	public CostCloneResult(int groupNo, HashMap<String, Integer> paraTierMap,
			double costPoint, double manHour) {
		super();
		this.groupNo = groupNo;
		this.paraTierMap = paraTierMap;
		this.costPoint = costPoint;
		this.manHour = manHour;
	}

	public int getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}

	public HashMap<String, Integer> getParaTierMap() {
		return paraTierMap;
	}

	public void setParaTierMap(HashMap<String, Integer> paraTierMap) {
		this.paraTierMap = paraTierMap;
	}

	public double getCostPoint() {
		return costPoint;
	}

	public void setCostPoint(double costPoint) {
		this.costPoint = costPoint;
	}

	public double getManHour() {
		return manHour;
	}

	public void setManHour(double manHour) {
		this.manHour = manHour;
	}

	public int getParaCount() {
		return paraCount;
	}

	public void setParaCount(int paraCount) {
		this.paraCount = paraCount;
	}

	public double getCombinedOutput() {
		return combinedOutput;
	}

	public void setCombinedOutput(double combinedOutput) {
		this.combinedOutput = combinedOutput;
	}

	public double getTotalCostPoint() {
		return totalCostPoint;
	}

	public void setTotalCostPoint(double totalCostPoint) {
		this.totalCostPoint = totalCostPoint;
	}

	public int getTier_1() {
		return tier_1;
	}

	public void setTier_1(int tier_1) {
		this.tier_1 = tier_1;
	}
}
