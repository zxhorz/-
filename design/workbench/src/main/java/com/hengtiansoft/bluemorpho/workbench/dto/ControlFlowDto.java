package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ControlFlowDto implements Serializable, Comparable<ControlFlowDto> {

	private String graphName;
	private String graphPath;
	private String svgContent;

	public ControlFlowDto() {
	}

	public ControlFlowDto(String graphName, String graphPath, String svgContent) {
		super();
		this.graphName = graphName;
		this.graphPath = graphPath;
		this.svgContent = svgContent;
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public String getGraphPath() {
		return graphPath;
	}

	public void setGraphPath(String graphPath) {
		this.graphPath = graphPath;
	}

	public String getSvgContent() {
		return svgContent;
	}

	public void setSvgContent(String svgContent) {
		this.svgContent = svgContent;
	}

	@Override
	public int compareTo(ControlFlowDto o) {
		return this.graphName.compareTo(o.getGraphName());
	}

}
