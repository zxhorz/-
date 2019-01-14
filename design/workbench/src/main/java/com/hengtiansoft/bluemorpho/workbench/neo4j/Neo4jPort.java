package com.hengtiansoft.bluemorpho.workbench.neo4j;

/**
 * @Description: Neo4j server的一些配置信息
 * @author gaochaodeng
 * @date May 22, 2018
 */
public class Neo4jPort {
	private int httpPort;
	private int boltPort;
	private int httpsPort;
	private int shellPort;
	private int status = 0;
	// 暂时listenAddress和advertisedAddress都采取默认值
	private String listenAddress = "0.0.0.0";
	private String advertisedAddress = "localhost";
	
	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public int getBoltPort() {
		return boltPort;
	}

	public void setBoltPort(int boltPort) {
		this.boltPort = boltPort;
	}

	public int getHttpsPort() {
		return httpsPort;
	}

	public void setHttpsPort(int httpsPort) {
		this.httpsPort = httpsPort;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getListenAddress() {
		return listenAddress;
	}

	public void setListenAddress(String listenAddress) {
		this.listenAddress = listenAddress;
	}

	public String getAdvertisedAddress() {
		return advertisedAddress;
	}

	public void setAdvertisedAddress(String advertisedAddress) {
		this.advertisedAddress = advertisedAddress;
	}

	public int getShellPort() {
		return shellPort;
	}

	public void setShellPort(int shellPort) {
		this.shellPort = shellPort;
	}

}
