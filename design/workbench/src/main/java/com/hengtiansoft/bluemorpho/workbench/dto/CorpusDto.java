package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.AbbriviationDictCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.WordAndPhraseTagCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 3:22:38 PM
 */
@SuppressWarnings("serial")
public class CorpusDto implements Serializable {

	private List<AbbriviationDictCorpus> abbriviationDictCorpus;
	private List<WordAndPhraseTagCorpus> wordAndPhraseTagCorpus;

	public CorpusDto() {
		super();
	}

	public CorpusDto(List<AbbriviationDictCorpus> abbriviationDictCorpus,
			List<WordAndPhraseTagCorpus> wordAndPhraseTagCorpus) {
		super();
		this.abbriviationDictCorpus = abbriviationDictCorpus;
		this.wordAndPhraseTagCorpus = wordAndPhraseTagCorpus;
	}

	public List<AbbriviationDictCorpus> getAbbriviationDictCorpus() {
		return abbriviationDictCorpus;
	}

	public void setAbbriviationDictCorpus(
			List<AbbriviationDictCorpus> abbriviationDictCorpus) {
		this.abbriviationDictCorpus = abbriviationDictCorpus;
	}

	public List<WordAndPhraseTagCorpus> getWordAndPhraseTagCorpus() {
		return wordAndPhraseTagCorpus;
	}

	public void setWordAndPhraseTagCorpus(
			List<WordAndPhraseTagCorpus> wordAndPhraseTagCorpus) {
		this.wordAndPhraseTagCorpus = wordAndPhraseTagCorpus;
	}

}
