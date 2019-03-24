package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyInfoService {

    @Autowired
    private StudentRepository studentRepository;

    public Student myInfoGet(String id) {
        return studentRepository.findStudentById(id);
    }

    public boolean myInfoSave(Student student) {
        try{
            studentRepository.save(student);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
