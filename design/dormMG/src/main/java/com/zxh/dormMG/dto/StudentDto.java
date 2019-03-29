package com.zxh.dormMG.dto;

import com.zxh.dormMG.domain.Student;

import javax.persistence.*;
import java.util.Objects;

public class StudentDto {

    private String id;

    private String name;

    private String dorm;

    private String branch;

    private String tel;

    private String email;

    private String studentClass;

    private String pos;

    public StudentDto() {

    }

    public StudentDto(String id) {
        this.id = id;
    }

    public StudentDto(String id, String name, String dorm, String branch, String tel, String email, String studentClass, String pos) {
        this.id = id;
        this.name = name;
        this.dorm = dorm;
        this.branch = branch;
        this.tel = tel;
        this.email = email;
        this.studentClass = studentClass;
        this.pos = pos;
    }

    public StudentDto(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.dorm = student.getDorm().getId();
        this.branch = student.getBranch();
        this.tel = student.getTel();
        this.email = student.getEmail();
        this.studentClass = student.getStudentClass();
        this.pos = student.getPos();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getDormId() {
//        return dormId;
//    }
//
//    public void setDormId(String dormId) {
//        this.dormId = dormId;
//    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDorm() {
        return dorm;
    }

    public void setDorm(String dorm) {
        this.dorm = dorm;
    }

    @Override
    public boolean equals(Object o) {
        StudentDto inItem = (StudentDto) o;
        return Objects.equals(id, inItem.getId());
    }

}
