package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 28, 2018
 */
public class CostProgramResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String name;
	private int paraCount;
	private int loc;
	private int loop;
	private int conditionalStatements;
	private int tables;
	private int variables;
	private double complexityRatio;
	private double costPoint;
	private double manHour;
	private double complexity;
	private List<CostParagraphResult> paragraphResults = new ArrayList<CostParagraphResult>();

	public CostProgramResult() {
		super();
	}

	public CostProgramResult(String nodeId, String name, int loc,
			double complexity, int paraCount) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.loc = loc;
		this.complexity = complexity;
		this.paraCount = paraCount;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public double getComplexity() {
		return complexity;
	}

	public void setComplexity(double complexity) {
		this.complexity = complexity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParaCount() {
		return paraCount;
	}

	public void setParaCount(int paraCount) {
		this.paraCount = paraCount;
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

	public double getComplexityRatio() {
		return complexityRatio;
	}

	public void setComplexityRatio(double complexityRatio) {
		this.complexityRatio = complexityRatio;
	}

	public List<CostParagraphResult> getParagraphResults() {
		return paragraphResults;
	}

	public void setParagraphResults(List<CostParagraphResult> paragraphResults) {
		this.paragraphResults = paragraphResults;
	}
}
