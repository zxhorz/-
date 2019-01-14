package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @author <a href="chendonghuang@hengtiansoft.com">chendonghuang</a> Date: Dec
 *         27, 2018 5:24:48 PM
 */
@SuppressWarnings("serial")
public class LoginDto implements Serializable {

	private String userName;
	private String password;
	private String newPassword;
	private String captcha;

	public LoginDto() {
		super();
	}

	public LoginDto(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	public LoginDto(String userName, String password, String newPassword) {
        super();
        this.userName = userName;
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

}
