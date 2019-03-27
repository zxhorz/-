package com.zxh.dormMG.domain;

import javax.persistence.*;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "dorm")
//    @ManyToOne(fetch = FetchType.EAGER)
    private String dorm;

    @Column(name = "branch")
    private String branch;

    @Column(name = "tel")
    private String tel;

    @Column(name = "email")
    private String email;

    @Column(name = "class")
    private String studentClass;

    @Column(name = "dorm_pos")
    private String pos;

    public Student() {
    }

    public Student(String id, String name, String dorm, String branch, String tel, String email, String studentClass, String pos) {
        this.id = id;
        this.name = name;
        this.dorm = dorm;
        this.branch = branch;
        this.tel = tel;
        this.email = email;
        this.studentClass = studentClass;
        this.pos = pos;
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

    public String getDorm() {
        return dorm;
    }

    public void setDorm(String dorm) {
        this.dorm = dorm;
    }

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
}
