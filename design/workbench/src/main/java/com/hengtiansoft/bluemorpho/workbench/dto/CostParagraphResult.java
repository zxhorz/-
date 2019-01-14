package com.hengtiansoft.bluemorpho.workbench.dto;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 28, 2018
 */
public class CostParagraphResult {
	private String name;
	private int loc;
	private int loop;
	private int conditionalStatements;
	private int tables;
	private int variables;
	private double complexityRatio;
	private int cloneGroup;
	private int cloneTier;
	private double costPoint;
	private double manHour;

	public CostParagraphResult() {
		super();
	}

	public CostParagraphResult(String name, int loc, int loop,
			int conditionalStatements, int tables, int variables,
			double complexityRatio) {
		super();
		this.name = name;
		this.loc = loc;
		this.loop = loop;
		this.conditionalStatements = conditionalStatements;
		this.tables = tables;
		this.variables = variables;
		this.complexityRatio = complexityRatio;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLoc() {
		return loc;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	public int getLoop() {
		return loop;
	}

	public void setLoop(int loop) {
		this.loop = loop;
	}

	public int getConditionalStatements() {
		return conditionalStatements;
	}

	public void setConditionalStatements(int conditionalStatements) {
		this.conditionalStatements = conditionalStatements;
	}

	public int getTables() {
		return tables;
	}

	public void setTables(int tables) {
		this.tables = tables;
	}

	public int getVariables() {
		return variables;
	}

	public void setVariables(int variables) {
		this.variables = variables;
	}

	public double getComplexityRatio() {
		return complexityRatio;
	}

	public void setComplexityRatio(double complexityRatio) {
		this.complexityRatio = complexityRatio;
	}

	public int getCloneGroup() {
		return cloneGroup;
	}

	public void setCloneGroup(int cloneGroup) {
		this.cloneGroup = cloneGroup;
	}

	public int getCloneTier() {
		return cloneTier;
	}

	public void setCloneTier(int cloneTier) {
		this.cloneTier = cloneTier;
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
}
