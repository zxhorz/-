package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> studentList() {
        Iterable<Student> dorms = studentRepository.findAll();
        List<Student> list = new ArrayList<>();
        for (Student dorm : dorms) {
            list.add(dorm);
        }

        return list;
    }
}
