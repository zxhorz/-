package com.zxh.dormMG.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class PortUtil {

	private static final Logger LOGGER = Logger.getLogger(PortUtil.class);
	private static final Map<String,String> virtualMap = new HashMap<String,String>();

	public String getWbServerIp() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip;
		} catch (UnknownHostException e) {
			LOGGER.error(e);
			return "localhost";
		}	
	}

}
