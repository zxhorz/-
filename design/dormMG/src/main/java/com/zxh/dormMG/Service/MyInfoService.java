package com.zxh.dormMG.Service;

import com.zxh.dormMG.Domain.Dorm;
import com.zxh.dormMG.Domain.Student;
import com.zxh.dormMG.Repository.DormRepository;
import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
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

    public ResultDto<String> myInfoSave(Student student) {
        String id = student.getId();
        String dormId = student.getDorm();

        Dorm dorm = dormRepository.findDormById(dormId);
        List<Student> students = studentRepository.findStudentsByDorm(dorm.getId());
        dorm.setStudents(students);


        if (dorm.getRemain() == 0 && !dorm.getStudents().contains(student)) {
            return ResultDtoFactory.toAck("F", dorm.getId() + "寝室已满");
        }

        List<Integer> positions = new ArrayList<>();
        for (int i = 1; i <= dorm.getVolume(); i++)
            positions.add(i);
        for (Student temp : students) {
            if (!temp.equals(student))
                positions.remove(temp.getPos());
        }
        if (student.getPos() > dorm.getVolume() || !positions.contains(student.getPos()))
            return ResultDtoFactory.toAck("F", "无此可用床位");

        try {
            studentRepository.save(student);
            return ResultDtoFactory.toAck("S");
        } catch (Exception e) {
            return ResultDtoFactory.toAck("F", "保存错误");
        }
    }
}
