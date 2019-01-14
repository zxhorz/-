package com.hengtiansoft.bluemorpho.workbench.domain.corpus;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hengtiansoft.bluemorpho.workbench.dto.WordAndPhraseTagCorpusResponse;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:21:03 AM
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "word_and_phrase_tag_corpus")
public class WordAndPhraseTagCorpus implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "phrase")
	private String phrase;

	@Column(name = "tag")
	private String tag;

	@Column(name = "business_domain_id")
	private int businessDomainId;

	@Column(name = "organization_id")
	private int organizationId;

	@Column(name = "system_id")
	private int systemId;

	@Column(name = "code_type")
	private String codeType;

	@Column(name = "frequency")
	private String frequency;

	@Column(name = "system_rank")
	private String systemRank;

	@Column(name = "organization_rank")
	private String organizationRank;

	@Column(name = "business_domain_rank")
	private String businessDomainRank;

	@Column(name = "general_rank")
	private String generalRank;

	public WordAndPhraseTagCorpus() {
		super();
	}

	public WordAndPhraseTagCorpus(String phrase, String tag,
			int businessDomainId, int organizationId, int systemId,
			String codeType, String frequency,
			String systemRank, String organizationRank,
			String businessDomainRank, String generalRank) {
		super();
		this.phrase = phrase;
		this.tag = tag;
		this.businessDomainId = businessDomainId;
		this.organizationId = organizationId;
		this.systemId = systemId;
		this.codeType = codeType;
		this.frequency = frequency;
		this.systemRank = systemRank;
		this.organizationRank = organizationRank;
		this.businessDomainRank = businessDomainRank;
		this.generalRank = generalRank;
	}

	public WordAndPhraseTagCorpus(
			WordAndPhraseTagCorpusResponse wordAndPhraseTagCorpus) {
		super();
		this.phrase = wordAndPhraseTagCorpus.getPhrase();
		this.tag = wordAndPhraseTagCorpus.getTag();
		this.codeType = wordAndPhraseTagCorpus.getCodeType();
		this.frequency = String.valueOf(wordAndPhraseTagCorpus.getFrequency());
		this.systemRank = String.valueOf(wordAndPhraseTagCorpus.getSystemRank());
		this.organizationRank = String.valueOf(wordAndPhraseTagCorpus.getOrganizationRank());
		this.businessDomainRank = String.valueOf(wordAndPhraseTagCorpus.getBusinessDomainRank());
		this.generalRank = String.valueOf(wordAndPhraseTagCorpus.getGeneralRank());
		this.businessDomainId = wordAndPhraseTagCorpus.getBusinessDomainId();
		this.organizationId = wordAndPhraseTagCorpus.getOrganizationId();
		this.systemId = wordAndPhraseTagCorpus.getSystemId();
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

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getSystemRank() {
		return systemRank;
	}

	public void setSystemRank(String systemRank) {
		this.systemRank = systemRank;
	}

	public String getOrganizationRank() {
		return organizationRank;
	}

	public void setOrganizationRank(String organizationRank) {
		this.organizationRank = organizationRank;
	}

	public String getBusinessDomainRank() {
		return businessDomainRank;
	}

	public void setBusinessDomainRank(String businessDomainRank) {
		this.businessDomainRank = businessDomainRank;
	}

	public String getGeneralRank() {
		return generalRank;
	}

	public void setGeneralRank(String generalRank) {
		this.generalRank = generalRank;
	}

}
