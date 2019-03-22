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
 * @version 创建时间：Jun 22, 2018 11:16:11 AM
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "organizations_corpus")
public class OrganizationsCorpus implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "organization_name")
	private String organizationName;

	@Column(name = "business_domain")
	private String businessDomain;

	public OrganizationsCorpus() {
		super();
	}

	public OrganizationsCorpus(String organizationName, String businessDomain) {
		super();
		this.organizationName = organizationName;
		this.businessDomain = businessDomain;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getBusinessDomain() {
		return businessDomain;
	}

	public void setBusinessDomain(String businessDomain) {
		this.businessDomain = businessDomain;
	}

}
