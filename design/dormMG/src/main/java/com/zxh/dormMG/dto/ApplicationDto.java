package com.zxh.dormMG.dto;

public class ApplicationDto {
    private String name;
    private String email;
    private String subject;
    private String type;
    private String priority;
    private String content;

    public ApplicationDto() {
    }

    public ApplicationDto(String name, String email, String subject, String type, String priority, String content) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.type = type;
        this.priority = priority;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
