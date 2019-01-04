package com.zxh.dormMG.dto;

import java.io.Serializable;

public class RegisterDto implements Serializable{
    private String userName;
    private String password;

    public RegisterDto() {
        super();
    }

    public RegisterDto(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
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
}
