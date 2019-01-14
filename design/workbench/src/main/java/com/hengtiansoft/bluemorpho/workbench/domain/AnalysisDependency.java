package com.hengtiansoft.bluemorpho.workbench.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 7:06:13 PM
 */
@Entity
@Table(name = "analysis_dependency")
public class AnalysisDependency implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private String id;

	@Column(name = "relier_id")
	private String relierId;

	@Column(name = "reliered_id")
	private String relieredId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRelierId() {
		return relierId;
	}

	public void setRelierId(String relierId) {
		this.relierId = relierId;
	}

	public String getRelieredId() {
		return relieredId;
	}

	public void setRelieredId(String relieredId) {
		this.relieredId = relieredId;
	}

}
