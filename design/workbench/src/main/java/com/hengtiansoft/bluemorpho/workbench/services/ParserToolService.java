package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;

@Service
public class ParserToolService {
	private static final Logger logger = Logger
			.getLogger(ParserToolService.class);
	private static final String PARSER_TREE_GENERATOR= "ParserTreeGenerator-0.0.1-SNAPSHOT.jar";
	
	public String getParserTree(String preprocessedCode){
		Date date = new Date();
		String path = FilePathUtil.getTempPath() + "/"+date.getTime();
		try {
			FileUtils.writeStringToFile(new File(path), preprocessedCode);
		} catch (IOException e) {
			logger.error(e);
			return "";
		}
		List<String> parms = new ArrayList<>();
		parms.add(path);
		List<String> cmd = buildJavaJarCmd(PARSER_TREE_GENERATOR, parms);
		String toolPath = FilePathUtil.getToolPath();
		String parserTree = ProcessBuilderUtil.processBuilderForParserTool(cmd, toolPath);
		logger.info(parserTree);
		return parserTree;
	}

	private List<String> buildJavaJarCmd(String jarName,List<String> parms) {
		List<String> cmd = new ArrayList<>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(jarName);
		cmd.addAll(parms);
		return cmd;
	}
}
