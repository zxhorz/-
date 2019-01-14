package com.hengtiansoft.bluemorpho.workbench.neo4j;

import org.neo4j.server.CommunityBootstrapper;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date May 23, 2018
 */
public class Neo4jServer {
	private CommunityBootstrapper boot;
	private Neo4jPort neo4jPort;

	public Neo4jServer(CommunityBootstrapper boot, Neo4jPort neo4jPort) {
		super();
		this.boot = boot;
		this.neo4jPort = neo4jPort;
	}

	public CommunityBootstrapper getBoot() {
		return boot;
	}

	public void setBoot(CommunityBootstrapper boot) {
		this.boot = boot;
	}

	public Neo4jPort getNeo4jPort() {
		return neo4jPort;
	}

	public void setNeo4jPort(Neo4jPort neo4jPort) {
		this.neo4jPort = neo4jPort;
	}

}
