package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.WordAndPhraseTagCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:21:03 AM
 */
@SuppressWarnings("serial")
public class WordAndPhraseTagCorpusResponse implements Serializable {

	private int id;

	private String phrase;

	private String tag;

	private String businessDomain;

	private String organization;

	private String system;
	
	private int businessDomainId;

	private int organizationId;

	private int systemId;

	private String codeType;

	private float frequency;

	private float systemRank;

	private float organizationRank;

	private float businessDomainRank;

	private float generalRank;

	public WordAndPhraseTagCorpusResponse() {
		super();
	}

	public WordAndPhraseTagCorpusResponse(WordAndPhraseTagCorpus wptc) {
		super();
		this.id = wptc.getId();
		this.phrase = wptc.getPhrase();
		this.tag = wptc.getTag();
		this.codeType = wptc.getCodeType();
		this.frequency = Float.valueOf(wptc.getFrequency());
		this.systemRank = Float.valueOf(wptc.getSystemRank());
		this.organizationRank = Float.valueOf(wptc.getOrganizationRank());
		this.businessDomainRank = Float.valueOf(wptc.getBusinessDomainRank());
		this.generalRank = Float.valueOf(wptc.getGeneralRank());
		this.businessDomainId = wptc.getBusinessDomainId();
		this.organizationId = wptc.getOrganizationId();
		this.systemId = wptc.getSystemId();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getBusinessDomain() {
		return businessDomain;
	}

	public void setBusinessDomain(String businessDomain) {
		this.businessDomain = businessDomain;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public float getFrequency() {
		return frequency;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}

	public float getSystemRank() {
		return systemRank;
	}

	public void setSystemRank(float systemRank) {
		this.systemRank = systemRank;
	}

	public float getOrganizationRank() {
		return organizationRank;
	}

	public void setOrganizationRank(float organizationRank) {
		this.organizationRank = organizationRank;
	}

	public float getBusinessDomainRank() {
		return businessDomainRank;
	}

	public void setBusinessDomainRank(float businessDomainRank) {
		this.businessDomainRank = businessDomainRank;
	}

	public float getGeneralRank() {
		return generalRank;
	}

	public void setGeneralRank(float generalRank) {
		this.generalRank = generalRank;
	}
	public int getBusinessDomainId() {
		return businessDomainId;
	}

	public void setBusinessDomainId(int businessDomainId) {
		this.businessDomainId = businessDomainId;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

	public int getSystemId() {
		return systemId;
	}

	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}
}
