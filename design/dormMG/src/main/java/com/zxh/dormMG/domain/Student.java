package com.zxh.dormMG.Domain;

import com.zxh.dormMG.Repository.DormRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "student")
public class Student {

    @Autowired
    @Transient
    private DormRepository dormRepository;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "dorm")
////    @ManyToOne(fetch = FetchType.EAGER)
    private String dorm;
//    @Transient
//    @ManyToOne(fetch = FetchType.EAGER)
//    private Dorm dorm;

    @Column(name = "branch")
    private String branch;

    @Column(name = "tel")
    private String tel;

    @Column(name = "email")
    private String email;

    @Column(name = "class")
    private String studentClass;

    @Column(name = "dorm_pos")
    private Integer pos;

    public Student() {

    }

    public Student(String id) {
        this.id = id;
    }

    public Student(String id, String name, String dorm, String branch, String tel, String email, String studentClass, Integer pos) {
        this.id = id;
        this.name = name;
        this.dorm = dorm;
        this.branch = branch;
        this.tel = tel;
        this.email = email;
        this.studentClass = studentClass;
        this.pos = pos;
    }

    public Student(String id, String name, String branch, String tel, String email, String studentClass) {
        this.id = id;
        this.name = name;
        this.branch = branch;
        this.tel = tel;
        this.email = email;
        this.studentClass = studentClass;
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

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
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
        if(o==null || !(o instanceof Student))
            return false;
        Student inItem = (Student) o;
        return Objects.equals(id, inItem.getId());
    }

}
