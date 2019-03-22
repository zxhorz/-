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
 * @version 创建时间：Sep 26, 2018 10:24:54 AM
 */
@ServerEndpoint(value="/websocket/customScript/{runId}")
@Component
public class CustomScriptWebSocket {

	private static int onlineCount = 0;
	private static Map<String, CustomScriptWebSocket> clients = new ConcurrentHashMap<String, CustomScriptWebSocket>();
	private Session session;
	private String runId;

	@OnOpen
	public void onOpen(@PathParam("runId") String runId, Session session)
			throws IOException {

		this.runId = runId;
		this.session = session;

		addOnlineCount();
		clients.put(runId, this);
		System.out.println("runId : " + runId + " customScript websocket已连接.");
	}

	@OnClose
	public void onClose() throws IOException {
		clients.remove(runId);
		subOnlineCount();
	}

	@OnMessage
	public void onMessage(String message) throws IOException {
	}

	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	public void sendMessageTo(String message, String runId) throws IOException {
		for (CustomScriptWebSocket webSocket : clients.values()) {
			if (webSocket.runId.equals(runId))
				synchronized (webSocket) {
					webSocket.session.getBasicRemote().sendText(message);
				}
		}
	}
	
	public void sendMessageAll(String message) throws IOException {
		for (CustomScriptWebSocket item : clients.values()) {
			synchronized (item) {
				item.session.getAsyncRemote().sendText(message);
			}
		}
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		CustomScriptWebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		CustomScriptWebSocket.onlineCount--;
	}

	public static synchronized Map<String, CustomScriptWebSocket> getClients() {
		return clients;
	}
	
}
