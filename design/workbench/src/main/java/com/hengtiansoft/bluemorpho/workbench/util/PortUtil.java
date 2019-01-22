package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServer;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServerPool;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;

@Component
public class PortUtil {

	private static final Logger LOGGER = Logger.getLogger(PortUtil.class);
	private static final Map<String,String> virtualMap = new HashMap<String,String>();
	private static final long modifyTime = 0l;
	
	@Autowired
	Neo4jServerPool pool;	
	
	@Autowired
	OperationLogger operationLogger;
	
	@Autowired
	ProjectRepository projectRepository;
	
	/**
	 * 通过projectPath查找启动的neo4j server
	 *
	 * @param projectPath
	 * @return
	 */
	public int getBoltPort(String projectPath) {
		Neo4jServer server = null;
		try {
			server = pool.getDB(projectPath,
					FileStatusUtil.checkCode(projectPath));
		} catch (Exception e) {
			return -1;
		}
		if (server != null && server.getNeo4jPort() != null) {
			return server.getNeo4jPort().getBoltPort();
		} else {
			LOGGER.error("Error to get neo4j bolt connection port for project "
					+ projectPath);
		}
		return 0;
	}
	
	/**
	 * 通过projectPath查找启动的neo4j server
	 *
	 * @param projectPath
	 * @return
	 */
	public int getHttpPort(String projectPath) {
		Neo4jServer server = pool.getDB(projectPath,
				FileStatusUtil.checkCode(projectPath));
		if (server != null && server.getNeo4jPort() != null) {
			return server.getNeo4jPort().getHttpPort();
		} else {
			LOGGER.error("Error to get neo4j bolt connection port for project "
					+ projectPath);
		}
		return 0;
	}
	
	/**
	 * 通过projectId查找启动的neo4j server
	 *
	 * @param projectPath
	 * @return
	 */
	public String getHttpPortByProjectId(String projectId) {
		Project project = projectRepository.findOne(projectId);
		int port = getHttpPort(project.getPath());
		return String.valueOf(port);
	}

	/**
	 * 通过projectId查找启动的neo4j server
	 *
	 * @param projectPath
	 * @return
	 */
	public int getBoltPortByProjectId(int projectId) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		int port = getBoltPort(project.getPath());
		return port;
	}

	/**
	 * 通过projectId得到boltUrl
	 *
	 * @param projectId
	 * @return
	 */
	public String getBoltUrl(int projectId) {
		return getBoltUrl(String.valueOf(projectId));
	}

	public String getBoltUrl(String projectId) {
		Project project = projectRepository.findOne(projectId);
		int port = getBoltPort(project.getPath());
		if (port == -1) {
			// so not exists or not start.
			return "";
		}
		String ip = "localhost";
		try {
			ip = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e) {
			LOGGER.error(e);
			return "bolt://localhost:" + port;
		}
		return "bolt://" + ip + ":" + port;
	}

	public String getUrlWithBolt(int port) {
		String ip = "localhost";
		try {
			ip = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e) {
			LOGGER.error(e);
			return "bolt://localhost:" + port;
		}
		return "bolt://" + ip + ":" + port;
	}
	
	public String getWbServerIp() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress().toString();
			checkVirtualMap();
			if(virtualMap.containsKey(ip)) {
				return virtualMap.get(ip);
			}else {
				return ip;				
			}
		} catch (UnknownHostException e) {
			LOGGER.error(e);
			return "localhost";
		}	
	}
	
    public String getLocalPort(){
        try{
            MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            String port = objectNames.iterator().next().getKeyProperty("port");
            return port;
        }catch(MalformedObjectNameException e){
            LOGGER.error(e);
            return "8080";
        }
    }

	private synchronized void checkVirtualMap() {
		File file = FilePathUtil.getVirtualMap();
		if(null!=file&&file.lastModified()!=modifyTime) {
			extractVirtualMap(file);
		}
	}
	
	private static void extractVirtualMap(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] ips = tempString.split("\\|\\|");
				if(null!=ips&&ips.length>1) {
					virtualMap.put(ips[0], ips[1]);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
}
