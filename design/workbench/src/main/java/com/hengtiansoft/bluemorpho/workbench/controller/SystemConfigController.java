package com.hengtiansoft.bluemorpho.workbench.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.dto.MenuResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.dto.SummaryTabMap;
import com.hengtiansoft.bluemorpho.workbench.services.SystemConfigService;
import com.hengtiansoft.bluemorpho.workbench.util.UserUtil;

@Api(tags = { "SystemConfig" }, description = "the System Config API")
@Controller
@RequestMapping(value = "/config")
public class SystemConfigController extends AbstractController {
	private static final Logger LOGGER = Logger
			.getLogger(SystemConfigController.class);

	@Autowired
	SystemConfigService systemConfigService;

	@ApiOperation(value = "get menu list", nickname = "getMenuList", notes = "get menu list")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/menu", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<MenuResponse>> getMenuList() {
		try {
			List<MenuResponse> menus = systemConfigService.getMenuList();
			if(menus.isEmpty()){
				return ResultDtoFactory.toNack("Error in menu configuration");
			}
			return ResultDtoFactory.toAck("Successful", menus);
		} catch (Exception e) {
			return ResultDtoFactory.toNack(e.getMessage());
		}
	}
	
    @ApiOperation(value = "get current user", nickname = "getCurrentUser", notes = "get current user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = { "application/json" })
    @ResponseBody
    public ResultDto<String> getCurrentUser() {
        String userName = UserUtil.getUserName();
        return ResultDtoFactory.toAck(userName);
    }
	
	
	@ApiOperation(value = "get map(summary <-> tab)", nickname = "getTabMap", notes = "get tab map")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found") })
	@RequestMapping(value = "/summarytotab", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<SummaryTabMap>> getTabMap() {
		try {
			List<SummaryTabMap> map = systemConfigService.getSummaryTabMap();
			if(map.isEmpty()){
				return ResultDtoFactory.toNack("Error in configuration");
			}
			return ResultDtoFactory.toAck("Successful", map);
		} catch (Exception e) {
			return ResultDtoFactory.toNack(e.getMessage());
		}
	}
}
