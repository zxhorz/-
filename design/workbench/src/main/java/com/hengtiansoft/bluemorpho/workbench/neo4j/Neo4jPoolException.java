package com.hengtiansoft.bluemorpho.workbench.neo4j;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date May 22, 2018
 */
public class Neo4jPoolException extends RuntimeException {
	private static final long serialVersionUID = 5570694433497155947L;

	public Neo4jPoolException() {
		super();
	}

	public Neo4jPoolException(String message) {
		super(message);
	}

	public Neo4jPoolException(String message, Throwable cause) {
		super(message, cause);
	}

	public Neo4jPoolException(Throwable cause) {
		super(cause);
	}

}
