package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.AbbriviationDictCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.BusinessDomainsCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.OrganizationsCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.SystemsCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.WordAndPhraseTagCorpus;
import com.hengtiansoft.bluemorpho.workbench.dto.AbbriviationDictCorpusResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.CorpusImportResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.WordAndPhraseTagCorpusResponse;
import com.hengtiansoft.bluemorpho.workbench.repository.AbbriviationDictCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.BusinessDomainsCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.OrganizationsCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.SystemsCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.WordAndPhraseTagCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 1:26:40 PM
 */
@Service
public class CorpusService {

	private static final Logger LOGGER = Logger.getLogger(CorpusService.class);
	@Autowired
	private AbbriviationDictCorpusRepository abbriviationDictCorpusRepository;
	@Autowired
	private WordAndPhraseTagCorpusRepository wordAndPhraseTagCorpusRepository;
	@Autowired
	private BusinessDomainsCorpusRepository businessDomainsCorpusRepository;
	@Autowired
	private OrganizationsCorpusRepository organizationsCorpusRepository;
	@Autowired
	private SystemsCorpusRepository systemsCorpusRepository;
	
	private Map<String,String> orgMap = new HashMap<>();
	private Map<String,String> domainMap = new HashMap<>();
	private Map<String,String> systemMap = new HashMap<>();
	
	// 页面获取corpus数据用，需要分页
	public Page<AbbriviationDictCorpusResponse> getAbbriviationDictCorpusData(int page, int size) {
		buildMap();
		Page<AbbriviationDictCorpus> find = abbriviationDictCorpusRepository.findAll(new PageRequest(page - 1, size));
		Page<AbbriviationDictCorpusResponse> result = find.map(new Converter<AbbriviationDictCorpus, AbbriviationDictCorpusResponse>() {
			@Override
			public AbbriviationDictCorpusResponse convert(AbbriviationDictCorpus source) {
				AbbriviationDictCorpusResponse target = new AbbriviationDictCorpusResponse(source);
				target.setBusinessDomain(domainMap.get(String.valueOf(source.getBusinessDomainId())));
				target.setOrganization(orgMap.get(String.valueOf(source.getOrganizationId())));
				target.setSystem(systemMap.get(String.valueOf(source.getSystemId())));
				return target;
			}
		});
		return result;
	}
	
	// 下载corpus csv时用，不需要分页，直接查询所有数据
	public List<AbbriviationDictCorpusResponse> getAbbriviationDictCorpusData() {
		List<AbbriviationDictCorpusResponse> result = new ArrayList<AbbriviationDictCorpusResponse>();
		buildMap();
		Iterator<AbbriviationDictCorpus> iterator = abbriviationDictCorpusRepository.findAll().iterator();
		while(iterator.hasNext()) {
			AbbriviationDictCorpus next = iterator.next();
			AbbriviationDictCorpusResponse abb = buildAbbriviationDictCorpusResponse(next);
			result.add(abb);
		}
		return result;
	}
	
	public Page<WordAndPhraseTagCorpusResponse> getWordAndPhraseTagCorpusData(int page, int size) {
		Page<WordAndPhraseTagCorpus> find = wordAndPhraseTagCorpusRepository.findAll(new PageRequest(page - 1, size));
		Page<WordAndPhraseTagCorpusResponse> result = find.map(new Converter<WordAndPhraseTagCorpus, WordAndPhraseTagCorpusResponse>() {
			@Override
			public WordAndPhraseTagCorpusResponse convert(WordAndPhraseTagCorpus source) {
				WordAndPhraseTagCorpusResponse target = new WordAndPhraseTagCorpusResponse(source);
				target.setBusinessDomain(domainMap.get(String.valueOf(source.getBusinessDomainId())));
				target.setOrganization(orgMap.get(String.valueOf(source.getOrganizationId())));
				target.setSystem(systemMap.get(String.valueOf(source.getSystemId())));
				return target;
			}
		});
		return result;
	}
	
	public List<WordAndPhraseTagCorpusResponse> getWordAndPhraseTagCorpusData() {
		List<WordAndPhraseTagCorpusResponse> result = new ArrayList<WordAndPhraseTagCorpusResponse>();
		Iterator<WordAndPhraseTagCorpus> iterator = wordAndPhraseTagCorpusRepository.findAll().iterator();
		while (iterator.hasNext()) {
			WordAndPhraseTagCorpus next = iterator.next();
			WordAndPhraseTagCorpusResponse res = buildWordAndPhraseTagCorpusResponse(next);
			result.add(res);
		}
		return result;
	}

	private WordAndPhraseTagCorpusResponse buildWordAndPhraseTagCorpusResponse(
			WordAndPhraseTagCorpus abbIter) {
		WordAndPhraseTagCorpusResponse next = new WordAndPhraseTagCorpusResponse(abbIter);
		next.setBusinessDomain(domainMap.get(String.valueOf(abbIter.getBusinessDomainId())));
		next.setOrganization(orgMap.get(String.valueOf(abbIter.getOrganizationId())));
		next.setSystem(systemMap.get(String.valueOf(abbIter.getSystemId())));
		return next;
	}

	public AbbriviationDictCorpus addOrModifyAbbCorpus(AbbriviationDictCorpusResponse abbriviationDictCorpus) {
		AbbriviationDictCorpus abbr = new AbbriviationDictCorpus(abbriviationDictCorpus);
		if(abbriviationDictCorpus.getId()!=0){
			abbr.setId(abbriviationDictCorpus.getId());
		}
		abbr = abbriviationDictCorpusRepository.save(abbr);
		return abbr;
	}

	/*private AbbriviationDictCorpus buildAbbriviationDictCorpus(
			AbbriviationDictCorpusResponse abbriviationDictCorpus) {
		AbbriviationDictCorpus abbr = new AbbriviationDictCorpus(abbriviationDictCorpus);
		if(abbriviationDictCorpus.getId()!=0){
			abbr.setId(abbriviationDictCorpus.getId());
		}
		buildMap2();
		abbr.setBusinessDomainId(Integer.valueOf(domainMap.get(abbriviationDictCorpus.getBusinessDomain())).intValue());
		abbr.setSystemId(Integer.valueOf(systemMap.get(abbriviationDictCorpus.getSystem())).intValue());
		abbr.setOrganizationId(Integer.valueOf(orgMap.get(abbriviationDictCorpus.getOrganization())).intValue());
		return abbr;
	}*/
	
	/*private WordAndPhraseTagCorpus buildAbbriviationDictCorpus(
			WordAndPhraseTagCorpusResponse wordAndPhraseTagCorpus) {
		WordAndPhraseTagCorpus word = new WordAndPhraseTagCorpus(wordAndPhraseTagCorpus);
		if(wordAndPhraseTagCorpus.getId()!=0){
			word.setId(wordAndPhraseTagCorpus.getId());
		}
		buildMap2();
		word.setBusinessDomainId(Integer.valueOf(domainMap.get(wordAndPhraseTagCorpus.getBusinessDomain())).intValue());
		word.setSystemId(Integer.valueOf(systemMap.get(wordAndPhraseTagCorpus.getSystem())).intValue());
		word.setOrganizationId(Integer.valueOf(orgMap.get(wordAndPhraseTagCorpus.getOrganization())).intValue());
		return word;
	}*/

	public WordAndPhraseTagCorpus addOrModifyWordCorpus(WordAndPhraseTagCorpusResponse wordAndPhraseTagCorpus) {
		WordAndPhraseTagCorpus word = new WordAndPhraseTagCorpus(wordAndPhraseTagCorpus);
		if(wordAndPhraseTagCorpus.getId()!=0){
			word.setId(wordAndPhraseTagCorpus.getId());
		}
		word = wordAndPhraseTagCorpusRepository.save(word);
		return word;
	}

	public void deleteAbbCorpus(int id) throws Exception {
		abbriviationDictCorpusRepository.delete(new Integer(id));
	}

	public void deleteWordCorpus(int id) {
		wordAndPhraseTagCorpusRepository.delete(new Integer(id));
	}

	public CorpusImportResponse batchImportAbbDict(HttpServletRequest request) throws ServletException, IOException {
		String tempFilePath = createLocalFile(request);
		if ("".equals(tempFilePath)) {
			return null;
		} else {
			CorpusImportResponse res = importAbbDict(tempFilePath);
			new File(tempFilePath).delete();
			return res;
		}
	}
	
	public CorpusImportResponse batchImportWordAndPhraseTag(HttpServletRequest request) throws ServletException, IOException  {
		String tempFilePath = createLocalFile(request);
		if ("".equals(tempFilePath)) {
			return null;
		} else {
			CorpusImportResponse res = importWord(tempFilePath);
			new File(tempFilePath).delete();
			return res;
		}
	}
	
	private CorpusImportResponse importAbbDict(String csvpath) {
		int success = 0;
		int total = 0;
		try {
			CsvReader csvReader = new CsvReader(csvpath);
			csvReader.setSafetySwitch(false);
			csvReader.readHeaders();
			while (csvReader.readRecord()) {
				String abbr = csvReader.get("Abbr");
				String fullPhrase = csvReader.get("Full Phrase");
				if (abbr != null && !"".equals(abbr) &&
						fullPhrase != null && !"".equals(fullPhrase)) {
					total++;
				} else {
					continue;
				}
				
				int domainId = 0;
				int orgId = 0;
				int sysId = 0;
				String business_domain = csvReader.get("Business Domain");
				BusinessDomainsCorpus find1 = businessDomainsCorpusRepository.findByName(business_domain);
				if (find1 == null) {
					continue;
				} else {
					domainId = find1.getId();
				}
				String organization = csvReader.get("Organization");
				OrganizationsCorpus find2 = organizationsCorpusRepository.findByName(organization);
				if (find2 == null) {
					continue;
				} else {
					orgId = find2.getId();
				}
				String system = csvReader.get("System");
				SystemsCorpus find3 = systemsCorpusRepository.findByName(system);
				if (find3 == null) {
					continue;
				} else {
					sysId = find3.getId();
				}
				String codeType = csvReader.get("Code Type");
				String frequency = csvReader.get("Frequency");
				String systemRank = csvReader.get("System Rank");
				String organizationRank = csvReader.get("Organization Rank");
				String businessDomainRank = csvReader.get("Business Domain Rank");
				String generalRank = csvReader.get("General Rank");
				AbbriviationDictCorpus abb = new AbbriviationDictCorpus(abbr, fullPhrase,
						domainId, orgId, sysId, codeType, frequency, systemRank,
						organizationRank, businessDomainRank, generalRank);
				abbriviationDictCorpusRepository.save(abb);
				success++;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		LOGGER.info("total " + total + " rows, " + success + " rows import success.");
		return new CorpusImportResponse(success, total - success);
	}
	
	private CorpusImportResponse importWord(String csvpath) {
		int success = 0;
		int total = 0;
		try {
			CsvReader csvReader = new CsvReader(csvpath);
			csvReader.setSafetySwitch(false);
			csvReader.readHeaders();
			while (csvReader.readRecord()) {
				String phrase = csvReader.get("Phrase");
				if (phrase != null && !"".equals(phrase)) {
					total++;
				} else {
					continue;
				}
				String tag = csvReader.get("Tag");
				int domainId = 0;
				int orgId = 0;
				int sysId = 0;
				String business_domain = csvReader.get("Business Domain");
				BusinessDomainsCorpus find1 = businessDomainsCorpusRepository.findByName(business_domain);
				if (find1 == null) {
					continue;
				} else {
					domainId = find1.getId();
				}
				String organization = csvReader.get("Organization");
				OrganizationsCorpus find2 = organizationsCorpusRepository.findByName(organization);
				if (find2 == null) {
					continue;
				} else {
					orgId = find2.getId();
				}
				String system = csvReader.get("System");
				SystemsCorpus find3 = systemsCorpusRepository.findByName(system);
				if (find3 == null) {
					continue;
				} else {
					sysId = find3.getId();
				}
				String codeType = csvReader.get("Code Type");
				String frequency = csvReader.get("Frequency");
				String systemRank = csvReader.get("System Rank");
				String organizationRank = csvReader.get("Organization Rank");
				String businessDomainRank = csvReader.get("Business Domain Rank");
				String generalRank = csvReader.get("General Rank");
				WordAndPhraseTagCorpus word = new WordAndPhraseTagCorpus(phrase, tag,
						domainId, orgId, sysId, codeType, frequency,
						systemRank, organizationRank, businessDomainRank, generalRank);
				wordAndPhraseTagCorpusRepository.save(word);
				success++;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		LOGGER.info("total " + total + " rows, " + success + " rows import success.");
		return new CorpusImportResponse(success, total - success);
	}

	private String createLocalFile(HttpServletRequest request) {
		String path = "";
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		//先判断request中是否包涵multipart类型的数据，
		if (multipartResolver.isMultipart(request)) {
			//再将request中的数据转化成multipart类型的数据
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				//这里的name为fileItem的alias属性值，相当于form表单中name
				String name = (String) iter.next();
				//根据name值拿取文件
				MultipartFile file = multiRequest.getFile(name);
				if (file != null) {
					String fileName = file.getOriginalFilename();
					path = FilePathUtil.createUploadFile(fileName);
					//写文件到本地
					try {
						file.transferTo(new File(path));
					} catch (IOException e) {
						LOGGER.error(e);
						return path;
					}
				}
			}
		} else {
			return path;
		}
		return path;
	}
	
	private void buildMap() {
		Iterator<BusinessDomainsCorpus> domainList = businessDomainsCorpusRepository.findAll().iterator();
		Iterator<SystemsCorpus> systemList =  systemsCorpusRepository.findAll().iterator();
		Iterator<OrganizationsCorpus> orgList =  organizationsCorpusRepository.findAll().iterator();
		while (domainList.hasNext()) {
			BusinessDomainsCorpus next = domainList.next();
			if(!domainMap.containsValue(String.valueOf(next.getId()))){
				domainMap.put(String.valueOf(next.getId()), next.getDomainName());
			}
		}
		while (systemList.hasNext()) {
			SystemsCorpus next = systemList.next();
			if(!systemMap.containsValue(String.valueOf(next.getId()))){
				systemMap.put(String.valueOf(next.getId()), next.getSystemName());
			}
		}
		while (orgList.hasNext()) {
			OrganizationsCorpus next = orgList.next();
			if(!orgMap.containsValue(String.valueOf(next.getId()))){
				orgMap.put(String.valueOf(next.getId()), next.getOrganizationName());
			}
		}
	}

	/*private void buildMap2() {
		Iterator<BusinessDomainsCorpus> domainList = businessDomainsCorpusRepository.findAll().iterator();
		Iterator<SystemsCorpus> systemList =  systemsCorpusRepository.findAll().iterator();
		Iterator<OrganizationsCorpus> orgList =  organizationsCorpusRepository.findAll().iterator();
		while (domainList.hasNext()) {
			BusinessDomainsCorpus next = domainList.next();
			if(!domainMap.containsValue(next.getDomainName())){
				domainMap.put(next.getDomainName(),String.valueOf(next.getId()));
			}
		}
		while (systemList.hasNext()) {
			SystemsCorpus next = systemList.next();
			if(!systemMap.containsValue(next.getSystemName())){
				systemMap.put(next.getSystemName(),String.valueOf(next.getId()));
			}
		}
		while (orgList.hasNext()) {
			OrganizationsCorpus next = orgList.next();
			if(!orgMap.containsValue(next.getOrganizationName())){
				orgMap.put(next.getOrganizationName(),String.valueOf(next.getId()));
			}
		}
		
	}*/
	
	// 页面展示corpus用，需要分页
	public Page<WordAndPhraseTagCorpusResponse> queryWordAndPhraseTagCorpusData(
			int domain, int system, int organization, int page, int size) {
		if(domain==-1&&system==-1&&organization==-1){
			return getWordAndPhraseTagCorpusData(page, size);
		}
		buildMap();
		Page<WordAndPhraseTagCorpus> find = null;
		PageRequest pageRequest = new PageRequest(page - 1, size);
		if (domain != -1 && system == -1 && organization == -1) {
			find = wordAndPhraseTagCorpusRepository.findByDomainPageable(domain, pageRequest);
		} else if (domain == -1 && system != -1 && organization == -1) {
			find = wordAndPhraseTagCorpusRepository.findBySystemPageable(system, pageRequest);
		} else if (domain == -1 && system == -1 && organization != -1) {
			find = wordAndPhraseTagCorpusRepository.findByOrganizationPageable(organization, pageRequest);
		} else if (domain != -1 && system != -1 && organization == -1) {
			find = wordAndPhraseTagCorpusRepository.findByDomainAndSystemPageable(domain, system, pageRequest);
		} else if (domain != -1 && system == -1 && organization != -1) {
			find = wordAndPhraseTagCorpusRepository.findByDomainAndOrganizationPageable(domain, organization, pageRequest);
		} else if (domain == -1 && system != -1 && organization != -1) {
			find = wordAndPhraseTagCorpusRepository.findBySystemAndOrganizationPageable(system, organization, pageRequest);
		} else {
			find = wordAndPhraseTagCorpusRepository.findByDomainAndSystemAndOrganizationPageable(domain, system, organization, pageRequest);
		}
		Page<WordAndPhraseTagCorpusResponse> result = find.map(new Converter<WordAndPhraseTagCorpus, WordAndPhraseTagCorpusResponse>(){
			@Override
			public WordAndPhraseTagCorpusResponse convert(WordAndPhraseTagCorpus source) {
				WordAndPhraseTagCorpusResponse target = new WordAndPhraseTagCorpusResponse(source);
				target.setBusinessDomain(domainMap.get(String.valueOf(source.getBusinessDomainId())));
				target.setOrganization(orgMap.get(String.valueOf(source.getOrganizationId())));
				target.setSystem(systemMap.get(String.valueOf(source.getSystemId())));
				return target;
			}
		});
		return result;
	}
	
	// 下载corpus csv用，不需要分页，直接查询所有数据
	public List<WordAndPhraseTagCorpusResponse> queryWordAndPhraseTagCorpusData(
			int domain, int system, int organization) {
		List<WordAndPhraseTagCorpusResponse> result = new ArrayList<WordAndPhraseTagCorpusResponse>();
		if(domain==-1&&system==-1&&organization==-1){
			return getWordAndPhraseTagCorpusData();
		}
		buildMap();
		List<WordAndPhraseTagCorpus> find = null;
		if (domain != -1 && system == -1 && organization == -1) {
			find = wordAndPhraseTagCorpusRepository.findByDomain(domain);
		} else if (domain == -1 && system != -1 && organization == -1) {
			find = wordAndPhraseTagCorpusRepository.findBySystem(system);
		} else if (domain == -1 && system == -1 && organization != -1) {
			find = wordAndPhraseTagCorpusRepository.findByOrganization(organization);
		} else if (domain != -1 && system != -1 && organization == -1) {
			find = wordAndPhraseTagCorpusRepository.findByDomainAndSystem(domain, system);
		} else if (domain != -1 && system == -1 && organization != -1) {
			find = wordAndPhraseTagCorpusRepository.findByDomainAndOrganization(domain, organization);
		} else if (domain == -1 && system != -1 && organization != -1) {
			find = wordAndPhraseTagCorpusRepository.findBySystemAndOrganization(system, organization);
		} else {
			find = wordAndPhraseTagCorpusRepository.findByDomainAndSystemAndOrganization(domain, system, organization);
		}
		for (WordAndPhraseTagCorpus word : find) {
			WordAndPhraseTagCorpusResponse res = buildWordAndPhraseTagCorpusResponse(word);
			result.add(res);
		}
		return result;
	}

	public Page<AbbriviationDictCorpusResponse> queryAbbriviationDictCorpusData(
			int domain, int system, int organization, int page, int size) {
		if (domain == -1 && system == -1 && organization == -1) {
			 return getAbbriviationDictCorpusData(page, size);
		}
		buildMap();
		Page<AbbriviationDictCorpus> find = null;
		PageRequest pageRequest = new PageRequest(page - 1, size);
		if (domain != -1 && system == -1 && organization == -1) {
			find = abbriviationDictCorpusRepository.findByDomainPageable(domain, pageRequest);
		} else if (domain == -1 && system != -1 && organization == -1) {
			find = abbriviationDictCorpusRepository.findBySystemPageable(system, pageRequest);
		} else if (domain == -1 && system == -1 && organization != -1) {
			find = abbriviationDictCorpusRepository.findByOrganizationPageable(organization, pageRequest);
		} else if (domain != -1 && system != -1 && organization == -1) {
			find = abbriviationDictCorpusRepository.findByDomainAndSystemPageable(domain, system, pageRequest);
		} else if (domain != -1 && system == -1 && organization != -1) {
			find = abbriviationDictCorpusRepository.findByDomainAndOrganizationPageable(domain, organization, pageRequest);
		} else if (domain == -1 && system != -1 && organization != -1) {
			find = abbriviationDictCorpusRepository.findBySystemAndOrganizationPageable(system, organization, pageRequest);
		} else {
			find = abbriviationDictCorpusRepository.findByDomainAndSystemAndOrganizationPageable(domain, system, organization, pageRequest);
		}
		Page<AbbriviationDictCorpusResponse> result = find.map(new Converter<AbbriviationDictCorpus, AbbriviationDictCorpusResponse>() {
			@Override
			public AbbriviationDictCorpusResponse convert(AbbriviationDictCorpus source) {
				AbbriviationDictCorpusResponse target = new AbbriviationDictCorpusResponse(source);
				target.setBusinessDomain(domainMap.get(String.valueOf(source.getBusinessDomainId())));
				target.setOrganization(orgMap.get(String.valueOf(source.getOrganizationId())));
				target.setSystem(systemMap.get(String.valueOf(source.getSystemId())));
				return target;
			}
		});
		return result;
	}

	// 下载corpus csv时用，不需要分页，直接查询所有数据
	public List<AbbriviationDictCorpusResponse> queryAbbriviationDictCorpusData(
			int domain, int system, int organization) {
		List<AbbriviationDictCorpusResponse> result = new ArrayList<AbbriviationDictCorpusResponse>();
		if (domain == -1 && system == -1 && organization == -1) {
			 return getAbbriviationDictCorpusData();
		}
		buildMap();
		List<AbbriviationDictCorpus> find = null;
		if (domain != -1 && system == -1 && organization == -1) {
			find = abbriviationDictCorpusRepository.findByDomain(domain);
		} else if (domain == -1 && system != -1 && organization == -1) {
			find = abbriviationDictCorpusRepository.findBySystem(system);
		} else if (domain == -1 && system == -1 && organization != -1) {
			find = abbriviationDictCorpusRepository.findByOrganization(organization);
		} else if (domain != -1 && system != -1 && organization == -1) {
			find = abbriviationDictCorpusRepository.findByDomainAndSystem(domain, system);
		} else if (domain != -1 && system == -1 && organization != -1) {
			find = abbriviationDictCorpusRepository.findByDomainAndOrganization(domain, organization);
		} else if (domain == -1 && system != -1 && organization != -1) {
			find = abbriviationDictCorpusRepository.findBySystemAndOrganization(system, organization);
		} else {
			find = abbriviationDictCorpusRepository.findByDomainAndSystemAndOrganization(domain, system, organization);
		}
		for (AbbriviationDictCorpus abb : find) {
			AbbriviationDictCorpusResponse res = buildAbbriviationDictCorpusResponse(abb);
			result.add(res);
		}
		return result;
	}
	
	private AbbriviationDictCorpusResponse buildAbbriviationDictCorpusResponse(
			AbbriviationDictCorpus abbIter) {
		AbbriviationDictCorpusResponse next = new AbbriviationDictCorpusResponse(abbIter);
		next.setBusinessDomain(domainMap.get(String.valueOf(abbIter.getBusinessDomainId())));
		next.setOrganization(orgMap.get(String.valueOf(abbIter.getOrganizationId())));
		next.setSystem(systemMap.get(String.valueOf(abbIter.getSystemId())));
		return next;
	}

	public List<OrganizationsCorpus> getOrganizationList() {
		List<OrganizationsCorpus> organizationList = new ArrayList<OrganizationsCorpus>();
		Iterator<OrganizationsCorpus> organizationIter =  organizationsCorpusRepository.findAll().iterator();
		while (organizationIter.hasNext()) {
			OrganizationsCorpus next = organizationIter.next();
			organizationList.add(next);
		}
		return organizationList;
	}

	public List<BusinessDomainsCorpus> getDomainList() {
		List<BusinessDomainsCorpus> domainList = new ArrayList<BusinessDomainsCorpus>();
		Iterator<BusinessDomainsCorpus> domainIter =  businessDomainsCorpusRepository.findAll().iterator();
		while (domainIter.hasNext()) {
			BusinessDomainsCorpus next = domainIter.next();
			domainList.add(next);
		}
		return domainList;
	}

	public List<SystemsCorpus> getSystemList() {
		List<SystemsCorpus> systemList = new ArrayList<SystemsCorpus>();
		Iterator<SystemsCorpus> systemIter =  systemsCorpusRepository.findAll().iterator();
		while (systemIter.hasNext()) {
			SystemsCorpus next = systemIter.next();
			systemList.add(next);
		}	
		return systemList;
	}

	public void exportAbbrDictCorpusCsv(HttpServletResponse response, int domain, int system, int organization) {
		String suffix = getTempCsvFileNameSuffix(domain, system, organization);
		String tempFileName = "AbbrDict_" + suffix + ".csv";
		String tempFilePath = FilePathUtil.createTempDownloadFile(tempFileName);
		writeAbbrTempFile(tempFilePath, domain, system, organization);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", tempFileName));
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(tempFilePath);
			byte[] buffer = new byte[1024];
			os = response.getOutputStream();
			int len;
			while ((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.flush();
					os.close();
				}
				File temp = new File(tempFilePath);
				temp.delete();
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}
	
	public void exportWordCorpusCsv(HttpServletResponse response, int domain, int system, int organization) {
		String suffix = getTempCsvFileNameSuffix(domain, system, organization);
		String tempFileName = "WordAndTag_" + suffix + ".csv";
		String tempFilePath = FilePathUtil.createTempDownloadFile(tempFileName);
		writeWordAndTagTempFile(tempFilePath, domain, system, organization);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", tempFileName));
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(tempFilePath);
			byte[] buffer = new byte[1024];
			os = response.getOutputStream();
			int len;
			while ((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.flush();
					os.close();
				}
				File temp = new File(tempFilePath);
				temp.delete();
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

	private String getTempCsvFileNameSuffix(int domain, int system,
			int organization) {
		String domainName = domain < 0 ? "ALL" : businessDomainsCorpusRepository.findOne(domain).getDomainName();
		String orgName = organization < 0 ? "ALL" : organizationsCorpusRepository.findOne(organization).getOrganizationName();
		String sysName = system < 0 ? "ALL" : systemsCorpusRepository.findOne(system).getSystemName();
		String suffix = domainName + "_" + orgName + "_" + sysName;
		return suffix;
	}

	private void writeAbbrTempFile(String tempFilePath, int domain, int system, int organization) {
		CsvWriter wr = null;
		List<AbbriviationDictCorpusResponse> datas = queryAbbriviationDictCorpusData(domain, system, organization);
		try {
			wr = new CsvWriter(tempFilePath);
			String[] header = {"Abbr", "Full Phrase", "Business Domain","Business Domain Rank", 
					"Organization", "Organization Rank", "System", "System Rank", "Code Type", 
					"Frequency",  "General Rank"};                 
			wr.writeRecord(header);
			for (AbbriviationDictCorpusResponse data : datas) {
				String[] content = { data.getAbbr(), data.getFullPhrase(),
						data.getBusinessDomain(), String.valueOf(data.getBusinessDomainRank()),
						data.getOrganization(), String.valueOf(data.getOrganizationRank()),
						data.getSystem(), String.valueOf(data.getSystemRank()),data.getCodeType(), 
						String.valueOf(data.getFrequency()), String.valueOf(data.getGeneralRank())};
				wr.writeRecord(content);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			wr.flush();
			wr.close();
		}
	}
	
	private void writeWordAndTagTempFile(String tempFilePath, int domain, int system, int organization) {
		CsvWriter wr = null;
		List<WordAndPhraseTagCorpusResponse> datas = queryWordAndPhraseTagCorpusData(domain, system, organization);
		try {
			wr = new CsvWriter(tempFilePath);
			String[] header = {"Phrase", "Tag", "Business Domain","Business Domain Rank", 
					"Organization", "Organization Rank",  "System", "System Rank", 
					"Code Type", "Frequency",  "General Rank"};             
			wr.writeRecord(header);
			for (WordAndPhraseTagCorpusResponse data : datas) {
				String[] content = {data.getPhrase(), data.getTag(), data.getBusinessDomain(), 
						String.valueOf(data.getBusinessDomainRank()), data.getOrganization(), String.valueOf(data.getOrganizationRank()), 
						data.getSystem(), String.valueOf(data.getSystemRank()), data.getCodeType(), String.valueOf(data.getFrequency()), 
								String.valueOf(data.getGeneralRank())};
				wr.writeRecord(content);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			wr.flush();
			wr.close();
		}
	}

}
