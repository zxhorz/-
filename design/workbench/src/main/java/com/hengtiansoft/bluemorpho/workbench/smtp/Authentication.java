package com.hengtiansoft.bluemorpho.workbench.smtp;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author <a href="chendonghuang@hengtiansoft.com">chendonghuang</a> Date: Jan
 *         4, 2019 2:59:29 PM
 */
public class Authentication extends Authenticator {
	
	String username = null;
	String password = null;

	public Authentication() {
	}

	public Authentication(String username, String password) {
		this.username = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		PasswordAuthentication pa = new PasswordAuthentication(username, password);
		return pa;
	}
	
}
