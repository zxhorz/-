package com.zxh.dormMG.dto;

public class UserDto {
    private String id;
    private String userName;
    private String role;

    public UserDto() {
    }

    public UserDto(String id, String userName, String role) {
        this.id = id;
        this.userName = userName;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
