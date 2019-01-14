package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @author <a href="chendonghuang@hengtiansoft.com">chendonghuang</a> Date: Jan
 *         3, 2019 4:13:04 PM
 */
@SuppressWarnings("serial")
public class RegisterDto implements Serializable {

	private String email;

	public RegisterDto() {
		super();
	}

	public RegisterDto(String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
