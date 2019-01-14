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
 * @version 创建时间：Jun 22, 2018 11:14:02 AM
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "business_domains_corpus")
public class BusinessDomainsCorpus implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "domain_name")
	private String domainName;

	@Column(name = "sub_domain_of")
	private String subDomainOf;

	public BusinessDomainsCorpus() {
		super();
	}

	public BusinessDomainsCorpus(String domainName, String subDomainOf) {
		super();
		this.domainName = domainName;
		this.subDomainOf = subDomainOf;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getSubDomainOf() {
		return subDomainOf;
	}

	public void setSubDomainOf(String subDomainOf) {
		this.subDomainOf = subDomainOf;
	}

}
