package com.hengtiansoft.bluemorpho.workbench.neo4j.dao;

import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 22, 2018 2:06:10 PM
 */
//@Component
public class Neo4jDao implements AutoCloseable {

	private static final Logger LOGGER = Logger.getLogger(Neo4jDao.class);
	private Driver driver;
	
	public Neo4jDao() {
	}
	
	public Neo4jDao(String uri) {
		this.driver = GraphDatabase.driver(uri);
	}

	public Neo4jDao(String uri, String username, String password) {
		this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
	}
	
	public StatementResult executeReadCypher(final String cypher) {
		try (Session session = driver.session()) {
			StatementResult result = session.run(cypher);
			return result;
		}
	}
	
	public StatementResult executeReadCypher(final String cypher, Map<String, Object> properties) {
		try (Session session = driver.session()) {
			StatementResult result = session.run(cypher, properties);
			return result;
		}
	}
	
	public void executeWriteCypher(final String cypher) {
		try (Session session = driver.session()) {
			String info = session.writeTransaction(new TransactionWork<String>() {
				@Override
				public String execute(Transaction tx) {
					StatementResult result = tx.run(cypher);
					return result.single().get(0).asString();
				}
			});
			LOGGER.info(info);
		}
	}
	
	public void executeWriteCypher(final String cypher, final Map<String, Object> properties) {
		try (Session session = driver.session()) {
			String info = session.writeTransaction(new TransactionWork<String>() {
				@Override
				public String execute(Transaction tx) {
					StatementResult result = tx.run(cypher, properties);
					return result.single().get(0).asString();
				}
			});
			LOGGER.info(info);
		}
	}

	@Override
	public void close() {
		try {
			driver.close();
		} catch (Exception e) {
			LOGGER.error("Neo4j instance close exception!", e);
		}
	}

}
