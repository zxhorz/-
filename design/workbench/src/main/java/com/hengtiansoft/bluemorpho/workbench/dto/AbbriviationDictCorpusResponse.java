package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.AbbriviationDictCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:05:09 AM
 */
@SuppressWarnings("serial")
public class AbbriviationDictCorpusResponse implements Serializable {

	private int id;

	private String abbr;

	private String fullPhrase;

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

	public AbbriviationDictCorpusResponse() {
		super();
	}

	public AbbriviationDictCorpusResponse(AbbriviationDictCorpus adc) {
		super();
		this.id = adc.getId();
		this.abbr = adc.getAbbr();
		this.fullPhrase = adc.getFullPhrase();
		this.codeType = adc.getCodeType();
		this.frequency = Float.valueOf(adc.getFrequency());
		this.systemRank = Float.valueOf(adc.getSystemRank());
		this.organizationRank = Float.valueOf(adc.getOrganizationRank());
		this.businessDomainRank = Float.valueOf(adc.getBusinessDomainRank());
		this.generalRank = Float.valueOf(adc.getGeneralRank());
		this.businessDomainId = adc.getBusinessDomainId();
		this.organizationId = adc.getOrganizationId();
		this.systemId = adc.getSystemId();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAbbr() {
		return abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	public String getFullPhrase() {
		return fullPhrase;
	}

	public void setFullPhrase(String fullPhrase) {
		this.fullPhrase = fullPhrase;
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
