package com.zxh.dormMG.domain;

import com.zxh.dormMG.dto.ApplicationDto;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "date")
    private String date;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "email")
    private String email;

    @Column(name = "subject")
    private String subject;

    @Column(name = "type")
    private String type;

    @Column(name = "priority")
    private String priority;

    @Column(name = "content")
    private String content;

    @Column(name = "info")
    private String info;

    @Column(name = "status")
    private String status;

    public Application() {
    }

    public Application(String id) {
        this.id = id;
    }

    public Application(String id, String date, String studentId, String email, String subject, String type, String priority, String content, String info, String status) {
        this.id = id;
        this.date = date;
        this.studentId = studentId;
        this.email = email;
        this.subject = subject;
        this.type = type;
        this.priority = priority;
        this.content = content;
        this.info = info;
        this.status = status;
    }

    public Application(ApplicationDto applicationDto,Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        this.date = sdf.format(date);
        this.studentId = applicationDto.getStudentId();
        this.email = applicationDto.getEmail();
        this.subject = applicationDto.getSubject();
        this.type = applicationDto.getType();
        this.priority = applicationDto.getPriority();
        this.content = applicationDto.getContent();
        this.info = "";
        this.status = "isWaiting";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
