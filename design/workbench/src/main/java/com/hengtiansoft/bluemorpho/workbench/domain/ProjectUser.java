package com.hengtiansoft.bluemorpho.workbench.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description: 
 *               记录project被哪些user在使用，一个user使用一个project是对应的一条记录，status=0,1记录未使用和在使用
 * @author gaochaodeng
 * @date May 20, 2018
 */
@Entity
@Table(name = "project_user")
public class ProjectUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private String id;

	@Column(name = "project_id")
	private String projectId;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "status")
	private int status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
