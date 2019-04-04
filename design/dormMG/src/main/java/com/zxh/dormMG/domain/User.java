package com.zxh.dormMG.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "name")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "state")
    private String state;

    @Column(name = "send_time")
    private String time;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Role> roles;

    public User() {
    }

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(String id, String activationCode, String state) {
        this.id = id;
        this.activationCode = activationCode;
        this.state = state;
    }

    public User(String id, String username, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}