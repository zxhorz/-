package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 28, 2018
 */
public class CostParameters implements Serializable {
	private static final long serialVersionUID = 1L;
	private int projectId;
	private double loc = 1;
	private double loop = 1.1;
	private double conditionalStatements = 1.2;
	private double tables = 1.3;
	private double variables = 1.05;
	private double medianLoc = 30;
	private double medianLoops = 1;
	private double medianConditions = 1;
	private double medianTables = 1;
	private double medianVariables = 5;
	private double costPoint = 100;
	private double manHour = 1;
	private double availableTimeline = 5;
	private double availableBudget = 100;
	private double hourlyRate = 26;
	private double resourceNeeds;

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(loc).append("_");
		buffer.append(loop).append("_");
		buffer.append(conditionalStatements).append("_");
		buffer.append(tables).append("_");
		buffer.append(variables).append("_");
		buffer.append(medianLoc).append("_");
		buffer.append(medianLoops).append("_");
		buffer.append(medianConditions).append("_");
		buffer.append(medianTables).append("_");
		buffer.append(medianVariables).append("_");
		buffer.append(costPoint).append("_");
		buffer.append(manHour).append("_");
		buffer.append(availableTimeline).append("_");
		buffer.append(availableBudget).append("_");
		buffer.append(hourlyRate);
		// buffer.append(resourceNeeds);
		return buffer.toString();
	}

	public double getLoc() {
		return loc;
	}

	public void setLoc(double loc) {
		this.loc = loc;
	}

	public double getLoop() {
		return loop;
	}

	public void setLoop(double loop) {
		this.loop = loop;
	}

	public double getConditionalStatements() {
		return conditionalStatements;
	}

	public void setConditionalStatements(double conditionalStatements) {
		this.conditionalStatements = conditionalStatements;
	}

	public double getTables() {
		return tables;
	}

	public void setTables(double tables) {
		this.tables = tables;
	}

	public double getVariables() {
		return variables;
	}

	public void setVariables(double variables) {
		this.variables = variables;
	}

	public double getMedianLoc() {
		return medianLoc;
	}

	public void setMedianLoc(double medianLoc) {
		this.medianLoc = medianLoc;
	}

	public double getMedianLoops() {
		return medianLoops;
	}

	public void setMedianLoops(double medianLoops) {
		this.medianLoops = medianLoops;
	}

	public double getMedianConditions() {
		return medianConditions;
	}

	public void setMedianConditions(double medianConditions) {
		this.medianConditions = medianConditions;
	}

	public double getMedianTables() {
		return medianTables;
	}

	public void setMedianTables(double medianTables) {
		this.medianTables = medianTables;
	}

	public double getMedianVariables() {
		return medianVariables;
	}

	public void setMedianVariables(double medianVariables) {
		this.medianVariables = medianVariables;
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

	public double getAvailableTimeline() {
		return availableTimeline;
	}

	public void setAvailableTimeline(double availableTimeline) {
		this.availableTimeline = availableTimeline;
	}

	public double getAvailableBudget() {
		return availableBudget;
	}

	public void setAvailableBudget(double availableBudget) {
		this.availableBudget = availableBudget;
	}

	public double getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(double hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public double getResourceNeeds() {
		return resourceNeeds;
	}

	public void setResourceNeeds(double resourceNeeds) {
		this.resourceNeeds = resourceNeeds;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

}
