package com.hengtiansoft.bluemorpho.workbench.domain.corpus;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:17:54 AM
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "projects_corpus")
public class ProjectsCorpus implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "system")
	private String system;

	public ProjectsCorpus() {
		super();
	}

	public ProjectsCorpus(String system) {
		super();
		this.system = system;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

}
