package com.hengtiansoft.bluemorpho.workbench.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.CodeStatusResponse;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 24, 2018 5:09:34 PM
 */
@Api(tags = {"Host"}, description = "the host API")
@Controller
@RequestMapping(value = "/host")
public class HostController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(HostController.class);

	private static final String SOURCE = "SOURCE";
	
	@Autowired
	PortUtil portUtil;
	
	@Autowired
	ProjectRepository projectRepository;
	
	
	@ApiOperation(value = "Get remote file path in server",
			nickname = "getRemoteFilePath",
			notes = "get the remote file path in server")
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "remote file path"),
        @ApiResponse(code = 400, message = "fail in getting remote file path", response = Error.class) })
	@RequestMapping(value = "/remotepath", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<CodeStatusResponse> getRemoteFilePath(@RequestParam("projectId") String projectId) {
		try {
			Project project = projectRepository.findOne(projectId);
			if(null==project){
				return ResultDtoFactory.toNack("No project existed");	
			}
//			String ip = InetAddress.getLocalHost().getHostAddress().toString();
//			if(ip.indexOf("10.10.22.8")>-1) {
//				ip = ip.replace("10.10.22.8","172.16.128.110");
//			}else if(ip.indexOf("10.10.22.14")>-1){
//				ip = ip.replace("10.10.22.14","172.16.131.189");
//			}
			String ip = portUtil.getWbServerIp();
			File file = new File(project.getPath());
			if(!file.exists()){
				return ResultDtoFactory.toNack("No source code path existed");
			}
			CodeStatusResponse csr = new CodeStatusResponse();
			String path = "\\\\" + ip +"\\project\\" + project.getName()+"\\sourcecode";
			String filePath = FilePathUtil.getPath(project.getPath(), SOURCE);
			Date lastModifiedTime = FileStatusUtil.getLastModifiedTime(filePath);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String codeVersion = FileStatusUtil.checkCode(project.getPath());
			if(null==lastModifiedTime){
				csr.setLastModifiedTime("No Source Code");	
				csr.setCodeVersion("No Source Code");
			}else{
				csr.setLastModifiedTime(formatter.format(lastModifiedTime));
				csr.setCodeVersion(codeVersion);
			}
			csr.setPath(path);
			return ResultDtoFactory.toAck(StringUtils.EMPTY, csr);
		} catch (Exception e) {
			LOGGER.error(e);
			return ResultDtoFactory.toNack("error");
		}
	}
	
	@ApiOperation(value = "Get neo4j server IP of the specific proejct",
			nickname = "getServerIp",
			notes = "get the ip address of neo4j server")
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "neo4j IP"),
        @ApiResponse(code = 400, message = "fail in getting IP of neo4j server", response = Error.class) })
	@RequestMapping(value = "/neo4j/ip", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> neo4jIP(@RequestParam("projectId") String projectId){

		try {
//			String ip = InetAddress.getLocalHost().getHostAddress().toString();
//			if(ip.indexOf("10.10.22.8")>-1) {
//				ip = ip.replace("10.10.22.8","172.16.128.110");
//			}else if(ip.indexOf("10.10.22.14")>-1){
//				ip = ip.replace("10.10.22.14","172.16.131.189");
//			}
			String ip = portUtil.getWbServerIp();
			String port = portUtil.getHttpPortByProjectId(projectId);
			ip = "http://" + ip + ":" + port;
//			ip= "/neo4j/" +port; 
			return ResultDtoFactory.toAck(StringUtils.EMPTY, ip );
		} catch (Exception e) {
			LOGGER.error(e);
			return ResultDtoFactory.toNack("error",e.getMessage());
		}
	}

	@ApiOperation(value = "Get neo4j bolt address of the specific proejct", nickname = "getBoltUri", notes = "get the bolt address of neo4j server")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "neo4j bolt uri"),
			@ApiResponse(code = 400, message = "fail in getting bolt uri of neo4j server", response = Error.class) })
	@RequestMapping(value = "/neo4j/boltUri", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public ResultDto<List<String>> neo4jBoltUri(
			@RequestParam("projectId") String projectId) {
		List<String> result = new ArrayList<String>();
		String boltUri = portUtil.getBoltUrl(projectId);
		String autoTagResultPath = FilePathUtil
				.getTagResultPath();
		result.add(boltUri);
		result.add(autoTagResultPath);
		return ResultDtoFactory.toAck(StringUtils.EMPTY, result);
	}
	
	@ApiOperation(value = "Get server ip",
			nickname = "serverIp",
			notes = "get server ip")
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "server IP"),
        @ApiResponse(code = 400, message = "fail in getting IP of server", response = Error.class) })
	@RequestMapping(value = "/server/ip", method = RequestMethod.GET,produces = { "application/json" })
	@ResponseBody
	public ResultDto<String> serverIp(){
		try {
//			String ip = InetAddress.getLocalHost().getHostAddress().toString();
//			if(ip.indexOf("10.10.22.8")>-1) {
//				ip = ip.replace("10.10.22.8","172.16.128.110");
//			}else if(ip.indexOf("10.10.22.14")>-1){
//				ip = ip.replace("10.10.22.14","172.16.131.189");
//			}
			String ip = portUtil.getWbServerIp();
			return ResultDtoFactory.toAck(StringUtils.EMPTY, ip);
		} catch (Exception e) {
			LOGGER.error(e);
			return ResultDtoFactory.toNack("error",e.getMessage());
		}
	}
	
}
