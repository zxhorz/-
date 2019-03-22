package com.hengtiansoft.bluemorpho.workbench.neo4j;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hengtiansoft.bluemorpho.workbench.quartz.QuartzManager;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.JobTree;
import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.Pair;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 8, 2018 3:31:07 PM
 */
public class QuartzTest {

	@Test
	public void testConstructJobTree() {
		QuartzManager manager = new QuartzManager();
		List<Pair<String>> pairs = new ArrayList<Pair<String>>();
		pairs.add(new Pair<String>("1", "2"));
		pairs.add(new Pair<String>("1", "3"));
		pairs.add(new Pair<String>("1", "4"));
		pairs.add(new Pair<String>("1", "5"));
		pairs.add(new Pair<String>("1", "6"));
		pairs.add(new Pair<String>("2", "7"));
		pairs.add(new Pair<String>("3", "7"));
		pairs.add(new Pair<String>("4", "7"));
		pairs.add(new Pair<String>("5", "7"));
		pairs.add(new Pair<String>("6", "7"));
		pairs.add(new Pair<String>("1", "8"));
		pairs.add(new Pair<String>("1", "9"));
		List<String> analysisTypeIds = new ArrayList<String>();
		analysisTypeIds.add("1");
		analysisTypeIds.add("5");
		analysisTypeIds.add("6");
		analysisTypeIds.add("7");
		analysisTypeIds.add("8");
		String rootValue = manager.findTreeRoot(analysisTypeIds, pairs);
		String projectId = "1";
		String codeVersion = "1";
		String time = "2018-06-08 16:12:00";
		JobTree<String> jobTree = manager.constructDependencyTree(rootValue, analysisTypeIds, pairs, projectId, codeVersion, time);
		System.out.println(jobTree.getNodes().length);
	}
	
}
