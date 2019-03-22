package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.dto.SearchInfoRequest;
import com.hengtiansoft.bluemorpho.workbench.dto.SearchResult;
import com.hengtiansoft.bluemorpho.workbench.dto.SearchTagRequest;
import com.hengtiansoft.bluemorpho.workbench.dto.TagInfoRequest;
import com.hengtiansoft.bluemorpho.workbench.dto.TagResponse;
import com.hengtiansoft.bluemorpho.workbench.services.CodeBrowserService;
import com.hengtiansoft.bluemorpho.workbench.services.SearchService;

/**
 * @Description: search & tagging API
 * @author gaochaodeng
 * @date Jun 12, 2018
 */
@Api(tags = { "Search" }, description = "the search API")
@Controller
@RequestMapping(value = "/search")
public class SearchController extends AbstractController {
	private static final Logger LOGGER = Logger
			.getLogger(SearchController.class);
	@Autowired
	SearchService searchService;
	@Autowired
	CodeBrowserService codeBrowserService;

	@ApiOperation(value = "search program/paragraph by key/similarcode", nickname = "search", notes = "Search program/paragraph by key/similarcode")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/searchByKeyOrCode", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<SearchResult>> search(
			@RequestParam("projectId") int projectId,
			@RequestParam("queryCondition") String queryCondition,
			@RequestParam("condition") String condition) {
		LOGGER.info("Search program/paragraph by keys or simlar codes.");
		List<SearchResult> result = new ArrayList<SearchResult>();
		result = searchService.searchByKeywordsOrCodes(
				String.valueOf(projectId), queryCondition, condition);
		return ResultDtoFactory.toAck("", result);
	}

	@ApiOperation(value = "search program/paragraph with tags", nickname = "searchByTags", notes = "Search program/paragraph with tags")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/searchByTags", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	@ResponseBody
	public ResultDto<List<SearchResult>> searchByTags(
			@RequestBody SearchTagRequest tagInfo) {
		LOGGER.info("Search program/paragraph with tags.");
		List<SearchResult> result = new ArrayList<SearchResult>();
		result = searchService.searchByTags(tagInfo.getProjectId(),
				tagInfo.getCondition());
		return ResultDtoFactory.toAck("", result);
	}

	@ApiOperation(value = "save added or removed tag", nickname = "saveTag", notes = "Save tag by clicking tag")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/saveTag", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> saveTag(@RequestBody TagInfoRequest tagInfoRequest) {
		LOGGER.info("Save tags.");
		searchService.saveTag(tagInfoRequest);
		return ResultDtoFactory.toAck("Success", "Save tags sucessfully");
	}
	
	@ApiOperation(value = "Get program source code ", nickname = "programSourceCode", notes = "Get program source code")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/getProgramSourceCode", method = RequestMethod.POST, produces = { "application/json" },
	consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> getProgramSourceCode(@RequestBody SearchInfoRequest searchInfo) {
		LOGGER.info("Get selected program source code.");
		String content = codeBrowserService.getSourceCode(searchInfo.getProjectId(), "cobol/" + searchInfo.getSourceName());
		return ResultDtoFactory.toAck("Success", content);
	}

	@ApiOperation(value = "Get paragraph source code ", nickname = "paragraphSourceCode", notes = "Get paragraph source code")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/getParagraphSourceCode", method = RequestMethod.POST, produces = { "application/json" },
			consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> getParagraphSourceCode(@RequestBody SearchInfoRequest searchInfo) {
		LOGGER.info("Get selected paragraph source code.");
		String content = searchService.getParaSourceCode(searchInfo.getProjectId(), searchInfo.getSourceName());
		return ResultDtoFactory.toAck("Success", content);
	}

	@ApiOperation(value = "Get all tags ", nickname = "getAllTags", notes = "Get all tags")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/getAllTags", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<String>> getAllTags(
			@RequestParam("projectId") int projectId) {
		LOGGER.info("Get all tags.");
		List<String> tags = searchService.getAllTags(String.valueOf(projectId));
		return ResultDtoFactory.toAck("Success", tags);
	}

	@ApiOperation(value = "Get all selected tags ", nickname = "getAllSelectedTags", notes = "Get all seleceted tags")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/getAllSelectedTags", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<TagResponse> getAllSelectedTags(
			@RequestParam("projectId") int projectId,
			@RequestParam("selectedNames") List<String> selectedNames,
			@RequestParam("type") String type,
			@RequestParam("fromPage") String fromPage) {
		LOGGER.info("Get all selected tags.");
		TagResponse tagResponse = searchService.getAllSelectedTags(
				String.valueOf(projectId), selectedNames, type, fromPage);
		return ResultDtoFactory.toAck("Success", tagResponse);
	}

	@ApiOperation(value = "add tags", nickname = "addTags", notes = "Add tags for program/paragraph in searchcode page")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/addTags", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> addTags(@RequestBody TagInfoRequest tagInfoRequest) {
		LOGGER.info("Add tags for program/paragraph.");
		searchService.addTags(tagInfoRequest.getProjectId(),
				tagInfoRequest.getSelectedNames(), tagInfoRequest.getAddTags());
		return ResultDtoFactory.toAck("Success", "Add tags sucessfully");
	}

	@ApiOperation(value = "remove tags", nickname = "removeTags", notes = "Remove tags for program/paragraph")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/removeTags", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	@ResponseBody
	public ResultDto<String> removeTags(
			@RequestBody TagInfoRequest tagInfoRequest) {
		LOGGER.info("Remove tags for program/paragraph.");
		searchService.removeTags(tagInfoRequest.getProjectId(),
				tagInfoRequest.getSelectedNames(),
				tagInfoRequest.getDeleteTags());
		return ResultDtoFactory.toAck("Success", "Remove tags sucessfully");
	}
	
	@ApiOperation(value = "get search result from output file", nickname = "search", notes = "get search result from output file")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/searchresult", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<SearchResult>> getSearchResult(
			@RequestParam("projectId") String projectId) {
		List<SearchResult> result = searchService.getSearchResult(projectId);
		return ResultDtoFactory.toAck("", result);
	}
}
