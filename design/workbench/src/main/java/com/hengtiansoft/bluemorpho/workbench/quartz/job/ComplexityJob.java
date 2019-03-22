package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hengtiansoft.bluemorpho.workbench.enums.CypherMethod;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.websocket.ProgressBarWebSocket;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 6:49:02 PM
 */
public class ComplexityJob implements Job {

	private static final Logger LOGGER = Logger.getLogger(ComplexityJob.class);
	public static ProgressBarWebSocket webSocket = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String jobName = jobDetail.getKey().getName();
		String boltUrl = jobDataMap.getString("boltUrl");
		String projectId = jobDataMap.getString("projectId");
		// 开始分析模拟给个20%进度
//		try {
//			webSocket.sendMessageTo(jobName + "/20", projectId);
//		} catch (IOException e1) {
//			LOGGER.error("webSocket error in complexity job.", e1);
//			e1.printStackTrace();
//		}
		try {
			Neo4jDao neo4jDao = new Neo4jDao(boltUrl);
			//使用简化的圈复杂度计算，paragraph中的所有blockposition（b），blockposition之间的关系r（perform关系不算）：complexity = r-b+2
			//以后理论及算法跟新是需要更改此处算法。（目前在cypher中完成查询和计算）。
			neo4jDao.executeReadCypher(CypherMethod.PARAGRAPH_COMPLEXITY.toString());
			neo4jDao.close();
			jobDataMap.put("code", "0");
		} catch(Exception e) {
			LOGGER.info("Complexity exception");
			jobDataMap.put("code", "-1");
		}
	}

}
