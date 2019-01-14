package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.List;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jul 30, 2018
 */
public class ParagraphUseTableInfo {
	public List<String> getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    private String nodeId;
	private String paragraphName;
	private String paragraphId;
	private String programId;
	private String programName;
	private String programLocation;
	private String paraStartLine;
	private String paraEndLine;
	private String startLine;
	private String endLine;
	private String operation;
	private String operationFullName;
	private String tableName;
	// 有些语句是在copybook中的，跳转的时候，就跳转至copybook中对应的位置
	private String copybook;
	private String blockId;
	private List<String> sourceCode;

	public ParagraphUseTableInfo() {
		super();
	}

	public ParagraphUseTableInfo(String nodeId, String paragraphName,
			String paragraphId, String programId, String programName,
			String programLocation, String paraStartLine, String paraEndLine,
			String startLine, String endLine, String operation, String tableName, String copybook, String blockId) {
		super();
		this.nodeId = nodeId;
		this.paragraphName = paragraphName;
		this.paragraphId = paragraphId;
		this.programId = programId;
		this.programName = programName;
		this.programLocation = programLocation;
		this.paraStartLine = paraStartLine;
		this.paraEndLine = paraEndLine;
		this.startLine = startLine;
		this.endLine = endLine;
		this.operation = operation;
		this.operationFullName = getOpFullName(operation);
		this.tableName = tableName;
		this.copybook = copybook;
		this.blockId = blockId;
	}

	public ParagraphUseTableInfo(String nodeId, String paragraphName,
			String paragraphId, String programId, String programName,
			String operation, String blockId) {
		super();
		this.nodeId = nodeId;
		this.paragraphName = paragraphName;
		this.paragraphId = paragraphId;
		this.programId = programId;
		this.programName = programName;
		this.operation = operation;
		this.operationFullName = getOpFullName(operation);
		this.blockId = blockId;
	}

	public String getParagraphName() {
		return paragraphName;
	}

	public void setParagraphName(String paragraphName) {
		this.paragraphName = paragraphName;
	}

	public String getParagraphId() {
		return paragraphId;
	}

	public void setParagraphId(String paragraphId) {
		this.paragraphId = paragraphId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramLocation() {
		return programLocation;
	}

	public void setProgramLocation(String programLocation) {
		this.programLocation = programLocation;
	}

	public String getStartLine() {
		return startLine;
	}

	public void setStartLine(String startLine) {
		this.startLine = startLine;
	}

	public String getEndLine() {
		return endLine;
	}

	public void setEndLine(String endLine) {
		this.endLine = endLine;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
		this.operationFullName = getOpFullName(operation);
	}

	public String getParaStartLine() {
		return paraStartLine;
	}

	public void setParaStartLine(String paraStartLine) {
		this.paraStartLine = paraStartLine;
	}

	public String getParaEndLine() {
		return paraEndLine;
	}

	public void setParaEndLine(String paraEndLine) {
		this.paraEndLine = paraEndLine;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getCopybook() {
		return copybook;
	}

	public void setCopybook(String copybook) {
		this.copybook = copybook;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}
	
	public String getOpFullName(String op) {
		String fullName = "";
		switch (op) {
		case "DC":
			fullName = "DECLARE_CURSOR";
			break;
		case "R":
			fullName = "SELECT";
			break;
		case "U":
			fullName = "UPDATE";
			break;
		case "D":
			fullName = "DELETE";
			break;
		case "C":
			fullName = "INSERT";
			break;
		}
		return fullName;
	}

	public String getOperationFullName() {
		return operationFullName;
	}

	public void setOperationFullName(String operationFullName) {
		this.operationFullName = operationFullName;
	}

}
