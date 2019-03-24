package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.DormRepository;
import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.domain.Dorm;
import com.zxh.dormMG.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DormService {
    @Autowired
    private DormRepository dormRepository;

    @Autowired
    private StudentRepository studentRepository;
    public List<Dorm> dormList() {
        Iterable<Dorm> dorms = dormRepository.findAll();
        List<Dorm> list = new ArrayList<>();
        for (Dorm dorm:dorms) {
            list.add(dorm);
        }

        for (Dorm dorm:list) {
            List<Student> students = studentRepository.findStudentsByDorm(dorm.getId());
            dorm.setStudents(students);
        }
        return list;
    }
}
