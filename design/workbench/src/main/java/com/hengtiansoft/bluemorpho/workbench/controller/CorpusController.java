package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.AbbriviationDictCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.BusinessDomainsCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.OrganizationsCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.SystemsCorpus;
import com.hengtiansoft.bluemorpho.workbench.domain.corpus.WordAndPhraseTagCorpus;
import com.hengtiansoft.bluemorpho.workbench.dto.AbbriviationDictCorpusResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.CorpusImportResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.dto.WordAndPhraseTagCorpusResponse;
import com.hengtiansoft.bluemorpho.workbench.repository.BusinessDomainsCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.OrganizationsCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.SystemsCorpusRepository;
import com.hengtiansoft.bluemorpho.workbench.services.CorpusService;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 20, 2018 3:43:30 PM
 */
@Api(tags = { "Corpus" }, description = "the corpus API")
@Controller
@RequestMapping(value = "/corpus")
public class CorpusController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(CorpusController.class);
	@Autowired
	private CorpusService corpusService;
	@Autowired
	private BusinessDomainsCorpusRepository businessDomainsCorpusRepository;
	@Autowired
	private OrganizationsCorpusRepository organizationsCorpusRepository;
	@Autowired
	private SystemsCorpusRepository systemsCorpusRepository;
	
//	@ApiOperation(value = "Upload the batch import file of corpus", nickname = "fileUpload", notes = "upload the batch import file of corpus")
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
//			@ApiResponse(code = 201, message = "Created"),
//			@ApiResponse(code = 401, message = "Unauthorized"),
//			@ApiResponse(code = 403, message = "Forbidden"),
//			@ApiResponse(code = 404, message = "Not Found") })
//	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
//	@ResponseBody
//	public ResultDto<Object> fileUpload(/*HttpServletRequest request*/) {
//		LOGGER.info("upload file.....");
//		return ResultDtoFactory.toAck("");
//	}
	
//	@ApiOperation(value = "Query all corpus", nickname = "queryAllCorpus", notes = "query all corpus")
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
//			@ApiResponse(code = 201, message = "Created"),
//			@ApiResponse(code = 401, message = "Unauthorized"),
//			@ApiResponse(code = 403, message = "Forbidden"),
//			@ApiResponse(code = 404, message = "Not Found") })
//	@RequestMapping(value = "/queryAllCorpus", method = RequestMethod.GET,produces = { "application/json" })
//	@ResponseBody
//	public ResultDto<CorpusDto> queryAllCorpus() {
//		CorpusDto corpusDto = corpusService.queryAllCorpus();
//		return ResultDtoFactory.toAck("", corpusDto);
//	}
	
	@ApiOperation(value = "Delete abbriviation dict corpus", nickname = "deleteAbbCorpus", notes = "delete abbriviation dict corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/deleteAbbCorpus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<Object> deleteAbbCorpus(@RequestParam("corpusId") int id) {
		try {
			corpusService.deleteAbbCorpus(id);
			return ResultDtoFactory.toAck("success"); 
		} catch (Exception e) {
			LOGGER.error(e);
			return ResultDtoFactory.toNack("error"); 
		}
	}
	
	@ApiOperation(value = "Delete word and phrase tag corpus", 
			nickname = "deleteWordCorpus", notes = "delete word and phrase tag corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/deleteWordCorpus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<Object> deleteWordCorpus(@RequestParam("corpusId") int id) {
		try {
			corpusService.deleteWordCorpus(id);
			return ResultDtoFactory.toAck("success"); 
		} catch (Exception e) {
			LOGGER.error(e);
			return ResultDtoFactory.toAck("error"); 
		}
	}
	
	@ApiOperation(value = "Add or modify abbriviation dict corpus", 
			nickname = "addOrModifyAbbCorpus", notes = "add or modify abbriviation dict corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/addOrModifyAbbCorpus", method = RequestMethod.POST)
	@ResponseBody
	public ResultDto<AbbriviationDictCorpus> addOrModifyAbbCorpus(@RequestBody AbbriviationDictCorpusResponse abbriviationDictCorpus) {
		if(abbriviationDictCorpus.getAbbr()==null||
		   abbriviationDictCorpus.getAbbr().isEmpty()||
		   abbriviationDictCorpus.getFullPhrase()==null||
		   abbriviationDictCorpus.getFullPhrase().isEmpty()){
			return ResultDtoFactory.toNack("Abbr or FullPhrase can't be empty");
		}
		AbbriviationDictCorpus abb = corpusService.addOrModifyAbbCorpus(abbriviationDictCorpus);
		return ResultDtoFactory.toAck("added.", abb);
	}
	
	@ApiOperation(value = "Add or modify word and phrase tag corpus", nickname = "addOrModifyWordCorpus", notes = "add or modify word and phrase tag corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/addOrModifyWordCorpus", method = RequestMethod.POST,produces = { "application/json" },consumes={ "application/json" })
	@ResponseBody
	public ResultDto<WordAndPhraseTagCorpus> addOrModifyWordCorpus(@RequestBody WordAndPhraseTagCorpusResponse wordAndPhraseTagCorpus) {
		if(wordAndPhraseTagCorpus.getPhrase()==null||
				wordAndPhraseTagCorpus.getPhrase().isEmpty()){
					return ResultDtoFactory.toNack("Phrase can't be empty");
				}
		
		WordAndPhraseTagCorpus word = corpusService.addOrModifyWordCorpus(wordAndPhraseTagCorpus);
		return ResultDtoFactory.toAck("added.", word);
	}
	
	@ApiOperation(value = "Batch import abbriviation dict corpus", 
			nickname = "batchImportAbbDict", notes = "batch import abbriviation dict corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/batchImport/abbreviation_dict", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public ResultDto<CorpusImportResponse> batchImportAbbDict(HttpServletRequest request) {
		try {
			CorpusImportResponse res = corpusService.batchImportAbbDict(request);
			return ResultDtoFactory.toAck("", res);
		} catch (Exception e) {
			LOGGER.error(e);
			return ResultDtoFactory.toAck("");
		}
	}
	
	@ApiOperation(value = "Batch import word and phrase tag corpus", 
			nickname = "batchImportWordAndPhraseTag", notes = "batch import word and phrase tag corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/batchImport/word_phase", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public ResultDto<CorpusImportResponse> batchImportWordAndPhraseTag(HttpServletRequest request) {
		try {
			CorpusImportResponse res = corpusService.batchImportWordAndPhraseTag(request);
			return ResultDtoFactory.toAck("", res);
		} catch (Exception e) {
			LOGGER.error(e);
			return ResultDtoFactory.toAck("");
		}
	}
	
	@ApiOperation(value = "Export all abbriviation dict corpus", 
			nickname = "exportAbbrDictCorpus", notes = "export all abbriviation dict corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/exportAbbrDictCorpus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public void exportAbbrDictCorpusTest(HttpServletResponse response, @RequestParam("domain") int domain,
			@RequestParam("system") int system,
			@RequestParam("organization") int organization) {
		corpusService.exportAbbrDictCorpusCsv(response, domain, system, organization);
	}
	
	@ApiOperation(value = "Export all word and phrase tag corpus", 
			nickname = "exportWordAndPhraseTagCorpus", notes = "export all word and phrase tag corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/exportWordAndPhraseTagCorpus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public void exportWordAndPhraseTagCorpus(HttpServletResponse response, @RequestParam("domain") int domain,
			@RequestParam("system") int system,
			@RequestParam("organization") int organization) {
		corpusService.exportWordCorpusCsv(response, domain, system, organization);
	}
	

	@ApiOperation(value = "Get all business domain", 
			nickname = "getDomainList", notes = "Get all business domain")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
	@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/domainlist", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<BusinessDomainsCorpus>> getDomainList() {
		List<BusinessDomainsCorpus> domainList = corpusService.getDomainList();
		return ResultDtoFactory.toAck("", domainList);
	}
	
	@ApiOperation(value = "Get all system", 
			nickname = "getSystemList", notes = "Get all system")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
	@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/systemlist", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<SystemsCorpus>> getSystemList() {
		List<SystemsCorpus> systemList = corpusService.getSystemList();
		return ResultDtoFactory.toAck("", systemList);
	}
	
	@ApiOperation(value = "Get all organization", 
			nickname = "getOrganizationList", notes = "Get all organization")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
	@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/organizationlist", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<OrganizationsCorpus>> getOrganizationList() {
		List<OrganizationsCorpus> organizationList = corpusService.getOrganizationList();
		return ResultDtoFactory.toAck("", organizationList);
	}
	
	@ApiOperation(value = "Get all abbr corpus", 
			nickname = "getAbbrCorpusList", notes = "Get all abbr corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
	@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/abbrlist", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<AbbriviationDictCorpusResponse>> getAbbrCorpusList(@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<AbbriviationDictCorpusResponse> abb = corpusService.getAbbriviationDictCorpusData(page, size);
		return ResultDtoFactory.toAck("", abb);
	}
	
	@ApiOperation(value = "Get all word and phrase corpus", 
			nickname = "getWordPhraseCorpusList", notes = "Get all word and phrase corpus")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
	@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/wordphraselist", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<WordAndPhraseTagCorpusResponse>> getWordPhraseCorpusList(@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<WordAndPhraseTagCorpusResponse> wordPhrase = corpusService.getWordAndPhraseTagCorpusData(page, size);
		return ResultDtoFactory.toAck("", wordPhrase);
	}
	
	@ApiOperation(value = "Get word and phrase corpus by specific value", 
			nickname = "queryWordPhraseCorpus", notes = "Get word and phrase corpus by specific value")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
	@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/queryWordPhraseCorpus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<WordAndPhraseTagCorpusResponse>> queryWordPhraseCorpus(@RequestParam("domain") int domain,
			@RequestParam("system") int system, @RequestParam("organization") int organization,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<WordAndPhraseTagCorpusResponse> wordPhrase = corpusService.queryWordAndPhraseTagCorpusData(domain, system, organization, page, size);
		return ResultDtoFactory.toAck("", wordPhrase);
	}
	
	@ApiOperation(value = "Get all abbr corpus by specific value", 
			nickname = "queryAbbrCorpus", notes = "Get all abbr corpus by specific value")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/queryAbbrCorpus", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<Page<AbbriviationDictCorpusResponse>> queryAbbrCorpus(@RequestParam("domain") int domain,
			@RequestParam("system") int system, @RequestParam("organization") int organization, 
			@RequestParam("page") int page, @RequestParam("size") int size) {
		Page<AbbriviationDictCorpusResponse> abb = corpusService.queryAbbriviationDictCorpusData(domain, system, organization, page, size);
		return ResultDtoFactory.toAck("", abb);
	}
}
