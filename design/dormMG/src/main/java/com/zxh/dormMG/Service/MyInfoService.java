package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.DormRepository;
import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.domain.Dorm;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.dto.StudentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyInfoService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DormRepository dormRepository;

    public Student myInfoGet(String id) {
        return studentRepository.findStudentById(id);
    }

    public ResultDto<String> myInfoSave(StudentDto studentDto) {
        Student student = new Student(studentDto);
        String id = student.getId();
        Dorm dorm = student.getDorm();
        if (!dorm.getStudents().contains(student)) {
            if (dorm.getRemain() == 0)
                return ResultDtoFactory.toAck("F", dorm.getId() + "寝室已满");
        } else {
            List<Student> students = dorm.getStudents();
            List<String> positions = new ArrayList<>();
            for (int i = 1; i <= dorm.getVolume(); i++)
                positions.add(i + "");
            for (Student temp : students) {
                positions.remove(temp.getPos());
            }
            student.setPos(positions.get(0));
        }
        try {
            studentRepository.save(student);
            return ResultDtoFactory.toAck("S");
        } catch (Exception e) {
            return ResultDtoFactory.toAck("F", "保存错误");
        }
    }
}
