package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: Jcl step info
 * @author gaochaodeng
 * @date Jul 17, 2018
 */
public class JclStepItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String stepName;
	private String pgmName;
	private String procName;
	private String pgmId;
	private String procId;
	private String procLocation;
	private String pgmLocation;

	public JclStepItem() {
		super();
	}

	public JclStepItem(String nodeId, String stepName, String pgmName,
			String procName) {
		super();
		this.nodeId = nodeId;
		this.stepName = stepName;
		this.pgmName = pgmName;
		this.procName = procName;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getPgmName() {
		return pgmName;
	}

	public void setPgmName(String pgmName) {
		this.pgmName = pgmName;
	}

	public String getProcName() {
		return procName;
	}

	public void setProcName(String procName) {
		this.procName = procName;
	}

	public String getPgmId() {
		return pgmId;
	}

	public void setPgmId(String pgmId) {
		this.pgmId = pgmId;
	}

	public String getProcId() {
		return procId;
	}

	public void setProcId(String procId) {
		this.procId = procId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getProcLocation() {
		return procLocation;
	}

	public void setProcLocation(String procLocation) {
		this.procLocation = procLocation;
	}

	public String getPgmLocation() {
		return pgmLocation;
	}

	public void setPgmLocation(String pgmLocation) {
		this.pgmLocation = pgmLocation;
	}
}
