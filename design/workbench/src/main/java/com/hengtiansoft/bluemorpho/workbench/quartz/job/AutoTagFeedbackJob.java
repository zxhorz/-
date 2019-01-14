package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.hengtiansoft.bluemorpho.workbench.dto.AutoTagFeedback;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServer;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServerPool;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;

/**
 * @Description: automatic recommand
 *               tags，将有文件修改或者文件手动tag发生变化的记录保存下来，定时通知RT算法去学习训练。
 * @author gaochaodeng
 * @date Sep 6, 2018
 */
public class AutoTagFeedbackJob implements Job {
	private static final Logger LOGGER = Logger
			.getLogger(AutoTagFeedbackJob.class);
	@Autowired
	private Neo4jServerPool pool;
	@Autowired
	private PortUtil portUtil;

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// 关于auto tag feedback接口，Python端还未实现，先注释掉该部分功能
		try {
			LOGGER.info("Start feedback job.");
//			// 参数获取
//			List<AutoTagFeedback> feedbacks = FilePathUtil.readJson(
//					FilePathUtil.getAutoFeedbackPath(), AutoTagFeedback.class);
//			Map<String, String> serverMap = manageNeo4jServer(feedbacks);
//			// String jsonStr =
//			// "{\"projectId\": \"3\",\"params\": [{\"name\": \"MIPVW019\",\"type\": \"Program\",\"tags\": []}],\"neo4jUri\": \"bolt://172.16.17.52:7687\",\"outputPath\": \"D:/test11111.json\",\"organization\": \"aaa\",\"businessDomain\": \"bbb\",\"system\": \"ccc\"}";
//			String jsonStr = JSON.toJSONString(feedbacks);
//
//			// 获取ip地址
//			String ip = "localhost";
//			try {
//				ip = InetAddress.getLocalHost().getHostAddress().toString();
//			} catch (UnknownHostException e) {
//				LOGGER.error(e);
//			}
//			String urlStr = "http://" + ip + ":5000/feedback/";
//
//			URL url = new URL(urlStr);
//			HttpURLConnection connection = (HttpURLConnection) url
//					.openConnection();
//			// 设置允许输出
//			connection.setDoOutput(true);
//			connection.setDoInput(true);
//			// 设置允许缓存
//			connection.setUseCaches(false);
//			connection.setInstanceFollowRedirects(true);
//			// 设置请求方式
//			connection.setRequestMethod("POST");
//			// 设置接收数据的格式
//			connection.setRequestProperty("Accept", "application/json");
//			// 设置发送数据的格式
//			connection.setRequestProperty("Content-Type", "application/json");
//			connection.connect();
//			OutputStreamWriter out = new OutputStreamWriter(
//					connection.getOutputStream(), "UTF-8"); // utf-8编码
//			// 写入请求字符串
//			out.write(jsonStr);
//			out.flush();
//			out.close();
//			// 请求返回的状态
//			if (connection.getResponseCode() == 200) {
//				LOGGER.info(urlStr + " 连接成功");
//				// 请求返回的数据
//				InputStream in = connection.getInputStream();
//				String message = null;
//				try {
//					byte[] data = new byte[in.available()];
//					in.read(data);
//					// 转成字符串
//					message = new String(data);
//					JSONObject object = JSON.parseObject(message);
//					if (object != null
//							&& object.get("msg") != null
//							&& "OK".equalsIgnoreCase(object.get("msg")
//									.toString())) {
//						// 收到请求，正确返回，关闭连接
//						LOGGER.info(urlStr + " 请求结束");
//						connection.disconnect();
//						// 将feedback.json文件清空
//						flushFeedback();
//						// 关闭该操作特意启动的neo4jServer
//						shutdown(serverMap);
//					}
//				} catch (Exception e) {
//					LOGGER.error(e);
//					connection.disconnect();
//				}
//			} else {
//				LOGGER.error("Failed to connect " + urlStr);
//				connection.disconnect();
//			}
		} catch (Exception e) {
			LOGGER.error("Failed to open http connecton", e);
		}
		LOGGER.info("End feedback job.");
	}

	private void flushFeedback() {
		FilePathUtil.writeFile(FilePathUtil.getAutoFeedbackPath(), "[]", false);
	}

	private Map<String, String> manageNeo4jServer(
			List<AutoTagFeedback> feedbacks) {
		// 保存的是此次操作启动的neo4j uri
		Map<String, String> neo4jMap = new HashMap<String, String>();
		for (AutoTagFeedback feedback : feedbacks) {
			String projectPath = feedback.getProjectPath();
			String codeVersion = feedback.getCodeVersion();
			// String neo4jServerKey = projectPath + "/" + codeVersion;
			Neo4jServer server = pool.checkDB(projectPath, codeVersion);
			if (server == null) {
				server = pool.getAndCreateDB(projectPath, codeVersion);
				String neo4jUri = portUtil.getUrlWithBolt(server.getNeo4jPort()
						.getBoltPort());
				feedback.setNeo4jUri(neo4jUri);
				neo4jMap.put(neo4jUri, projectPath + "/" + codeVersion);
			} else {
				feedback.setNeo4jUri(portUtil.getUrlWithBolt(server
						.getNeo4jPort().getBoltPort()));
			}
		}
		return neo4jMap;
	}

	private void shutdown(Map<String, String> serverMap) {
		int code = 1;
		for (Entry<String, String> entry : serverMap.entrySet()) {
			code = pool.destroyWithReturn(entry.getValue());
			if (code == 0) {
				continue;
			} else {
				// 关闭失败
			}
		}
	}
}
