package com.zxh.dormMG.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="dorm")
public class Dorm {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "volume")
    private Integer volume;

    @Transient
    private Integer remain;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dorm")
    @Transient
    private List<Student> students;

    public Dorm() {
    }

    public Dorm(String id, Integer volume) {
        this.id = id;
        this.volume = volume;
        this.remain = volume;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        int student = this.volume - this.remain;
        this.volume = volume;
        this.remain = volume - student;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

//    @Transient
    public Integer getRemain() {
        return remain;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        int size = students.size();
        this.students = students;
        this.remain = volume - size;
    }
}
