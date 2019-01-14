package com.hengtiansoft.bluemorpho.workbench.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Aug 28, 2018 5:00:30 PM
 */
@ServerEndpoint(value="/websocket/jobstatus/{projectId}")
@Component
public class JobStatusWebSocket {
	
	private static int onlineCount = 0;
	private static Map<String, JobStatusWebSocket> clients = new ConcurrentHashMap<String, JobStatusWebSocket>();
	private Session session;
	private String projectId;

	@OnOpen
	public void onOpen(@PathParam("projectId") String projectId, Session session)
			throws IOException {

		this.projectId = projectId;
		this.session = session;

		addOnlineCount();
		clients.put(projectId, this);
		System.out.println("projectId : " + projectId + " job status websocket已连接.");
	}

	@OnClose
	public void onClose() throws IOException {
		clients.remove(projectId);
		subOnlineCount();
	}

	@OnMessage
	public void onMessage(String message) throws IOException {
	}

	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	public void sendMessageTo(String message, String projectId) throws IOException {
		for (JobStatusWebSocket webSocket : clients.values()) {
			if (webSocket.projectId.equals(projectId))
				synchronized (webSocket) {
					webSocket.session.getBasicRemote().sendText(message);
				}
		}
	}

	public void sendMessageAll(String message) throws IOException {
		for (JobStatusWebSocket item : clients.values()) {
			synchronized (item) {
				item.session.getAsyncRemote().sendText(message);
			}
		}
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		JobStatusWebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		JobStatusWebSocket.onlineCount--;
	}

	public static synchronized Map<String, JobStatusWebSocket> getClients() {
		return clients;
	}
	
}
