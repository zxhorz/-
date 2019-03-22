package com.hengtiansoft.bluemorpho.workbench.neo4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.server.CommunityBootstrapper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.TemplateUtil;

@Component
public class Neo4jServerPool implements InitializingBean, DisposableBean {
	private static final Logger LOGGER = Logger
			.getLogger(Neo4jServerPool.class);
	private static final String NEO4J_FOLDER = "neo4j_server";
	private static final String TEMPLATE_CONF = "neo4j_conf.ftl";
	private ConcurrentHashMap<String, Neo4jServer> POOL = new ConcurrentHashMap<String, Neo4jServer>();

	private HashMap<String, Long> lastAccess = new HashMap<String, Long>();
	private HashMap<String, Object> locks = new HashMap<String, Object>();
	private List<Neo4jPort> ports = new ArrayList<Neo4jPort>();
	private volatile boolean shutdowned = false;
	private int maxSize = 30;
	private long timeout = 1800;
	private Timer timer = new Timer();
	private volatile int maxHttpPort = 7474;
	private volatile int maxBoltPort = 7687;
	private volatile int maxHttpsPort = 7473;
	private volatile int maxShellPort = 1337;

	public synchronized void shutdown() {
		if (shutdowned == false) {
			shutdowned = true;
			Collection<Neo4jServer> values = POOL.values();
			Iterator<Neo4jServer> iterator = values.iterator();
			while (iterator.hasNext()) {
				CommunityBootstrapper next = iterator.next().getBoot();
				if (next != null) {
					next.stop();
				}
			}
			POOL.clear();
			lastAccess.clear();
		}
	}

	public Neo4jServer getDB(String path, String codeVersion) {
		Neo4jServer instance = POOL.get(path + "/" + codeVersion);
		if (instance == null) {
			throw new IllegalArgumentException("db doesn't exist");
		}
		checkShutDown();
		return instance;
	}

	public Neo4jServer checkDB(String path, String codeVersion) {
		Neo4jServer instance = POOL.get(path + "/" + codeVersion);
		if (instance == null) {
			return null;
		}
		return instance;
	}

	public Neo4jServer getAndCreateDB(String path, String codeVersion) {
		String newPath = path + "/" + codeVersion;
		Neo4jServer instance = POOL.get(newPath);
		if (instance == null) {
			instance = createDBInstance(path, codeVersion);
		} else {
			boolean available = instance.getBoot().isRunning();
			if (!available) {
				destroy(newPath);
				instance = createDBInstance(path, codeVersion);
			} else {
				updateAccess(newPath);
			}
		}
		checkShutDown();
		return instance;
	}

	public void register(String path, Neo4jServer instance) {
		if (!POOL.containsKey(path)) {
			synchronized (getLock(path)) {
				if (!POOL.containsKey(path)) {
					POOL.put(path, instance);
					updateAccess(path);
				} else {
					LOGGER.warn("neo4j instance already exists.");
				}
			}
		} else {
			LOGGER.warn("neo4j instance already exists.");
		}
	}

	public void destroy(String path) {
		Neo4jServer instance = POOL.get(path);
		if (instance != null) {
			synchronized (getLock(path)) {
				instance = POOL.get(path);
				if (instance != null) {
                    Neo4jPort port= instance.getNeo4jPort();
                    for (Neo4jPort neo4jPort : ports) {
                        if(port.getBoltPort() == neo4jPort.getBoltPort())
                            neo4jPort.setStatus(0);
                    }
					instance.getBoot().stop();
					POOL.remove(path);
					lastAccess.remove(path);
					locks.remove(path);
				}
			}
		}
	}

	public int destroyWithReturn(String path) {
		int code = 1;
		Neo4jServer instance = POOL.get(path);
		if (instance != null) {
			synchronized (getLock(path)) {
				instance = POOL.get(path);
				if (instance != null) {
	                Neo4jPort port= instance.getNeo4jPort();
	                for (Neo4jPort neo4jPort : ports) {
	                    if(port.getBoltPort() == neo4jPort.getBoltPort())
	                        neo4jPort.setStatus(0);
                    }
					code = instance.getBoot().stop();
					POOL.remove(path);
					lastAccess.remove(path);
					locks.remove(path);
				}
			}
		}
		else {
		    code = 0;
		}
		return code;
	}

	private Neo4jServer createDBInstance(String path, String codeVersion) {
		String newPath = path + "/" + codeVersion;
		Neo4jServer instance = POOL.get(newPath);
		if (instance == null) {
			synchronized (getLock(newPath)) {
				instance = POOL.get(newPath);
				if (instance == null) {
					Neo4jPort port = getPorts();
					CommunityBootstrapper boot = new CommunityBootstrapper();
					String neo4jPath = FilePathUtil.getNeo4jPath(path,
							codeVersion);
					CommunityBootstrapper.start(boot,
							"--home-dir=" + neo4jPath, "--config-dir="
									+ placeNeo4jConfig(port, neo4jPath));
					instance = new Neo4jServer(boot, port);
					register(newPath, instance);
				}
			}
		}
		return instance;
	}

	private String placeNeo4jConfig(Neo4jPort port, String neo4jPath) {
		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("dbms_directories_data", databasePath);
		data.put("dbms_connector_http_listen_address", ":" + port.getHttpPort());
		data.put("dbms_connector_bolt_listen_address", ":" + port.getBoltPort());
		data.put("dbms_connector_https_listen_address",
				":" + port.getHttpsPort());
//		data.put("dbms_connectors_default_advertised_address",
//				port.getAdvertisedAddress());
		data.put("dbms_shell_port", String.valueOf(port.getShellPort()));
		File folder = new File(neo4jPath);
		String targetFilePath = folder.getAbsolutePath();
		targetFilePath = StringUtils.replace(targetFilePath, "\\", "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		TemplateUtil.generateFile(TEMPLATE_CONF, targetFilePath + "/"
				+ "neo4j.conf", data);
		return targetFilePath;
	}

	private Neo4jPort getPorts() {
		// 先查找ports中的port是否有空闲的
		Neo4jPort currentPort = new Neo4jPort();
		boolean hasIdlePort = false;
		for (Neo4jPort port : ports) {
			if (port.getStatus() == 0) {
				currentPort = port;
				hasIdlePort = true;
				break;
			}
		}
		if (hasIdlePort) {
			currentPort.setStatus(1);
		} else {
			currentPort.setHttpPort(maxHttpPort++);
			currentPort.setBoltPort(maxBoltPort++);
			currentPort.setHttpsPort(maxHttpsPort--);
			currentPort.setShellPort(maxShellPort--);
			currentPort.setStatus(1);
			ports.add(currentPort);
		}
		return currentPort;
	}

//	private String generateNeo4jConfPath(Neo4jPort port) {
//		String targetFilePath = null;
//		ApplicationHome home = new ApplicationHome(getClass());
//		File jarFile = home.getSource();
//		LOGGER.info("Jar Path:" + jarFile.getAbsolutePath());
//		if (jarFile.getAbsolutePath().endsWith(".jar")) {
//			targetFilePath = jarFile.getParentFile().getAbsolutePath();
//			targetFilePath = StringUtils.replace(targetFilePath, "\\", "/");
//			targetFilePath = targetFilePath + "/" + NEO4J_FOLDER;
//		} else {
//			try {
//				File file = ResourceUtils.getFile("classpath:" + NEO4J_FOLDER);
//				targetFilePath = file.getParent();
//			} catch (FileNotFoundException e) {
//				LOGGER.error("Can not find classpath:" + NEO4J_FOLDER, e);
//			}
//		}
//		targetFilePath = targetFilePath + "/" + "neo4j_" + port.getHttpPort();
//		File parentFile = new File(targetFilePath);
//		// 若neo4j_server目录不存在，则新建
//		if (!parentFile.getParentFile().exists()) {
//			parentFile.getParentFile().mkdirs();
//		}
//		// 若neo4j_7474之类目录不存在，则新建
//		if (!parentFile.exists()) {
//			parentFile.mkdirs();
//		}
//		return targetFilePath;
//	}

	private void updateAccess(String path) {
		if (lastAccess.containsKey(path)) {
			lastAccess.put(path, System.currentTimeMillis());
		}
	}

	private void checkShutDown() {
		if (shutdowned) {
			throw new Neo4jPoolException("neo4j db pool is shutdowned.");
		}
	}

	private Object getLock(String path) {
		if (!locks.containsKey(path)) {
			synchronized (locks) {
				if (!locks.containsKey(path)) {
					Object lock = new Object();
					locks.put(path, lock);
				}
			}
		}
		return locks.get(path);
	}

	public void clean() {
		long now = System.currentTimeMillis();
		synchronized (this) {
			Set<Entry<String, Long>> entrySet = lastAccess.entrySet();
			Iterator<Entry<String, Long>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<String, Long> next = iterator.next();
				Long value = next.getValue();
				if (value != null && (value - now) / 1000 > timeout) {
					destroy(next.getKey());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("destroy graph database service instance : "
								+ next.getKey());
					}
				}
			}
		}
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public void destroy() throws Exception {
		timer.cancel();
		shutdown();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		timer.schedule(new Neo4jDBPoolCleaner(this), 0, 1000 * 60);
	}

	class Neo4jDBPoolCleaner extends TimerTask {
		private Neo4jServerPool dbPool;

		public Neo4jDBPoolCleaner(Neo4jServerPool dbPool) {
			this.dbPool = dbPool;
		}

		@Override
		public void run() {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("neo4j db pool clean");
			}
			dbPool.clean();
		}
	}
}
