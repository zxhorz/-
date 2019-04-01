package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.DormRepository;
import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.domain.Dorm;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DormService {
    private static final Logger logger = Logger.getLogger(DormService.class);

    @Autowired
    private DormRepository dormRepository;

    @Autowired
    private StudentRepository studentRepository;
    public List<Dorm> dormList() {
        Iterable<Dorm> dorms = dormRepository.findAll();
        List<Dorm> list = new ArrayList<>();
        for (Dorm dorm:dorms) {
            List<Student> students = studentRepository.findStudentsByDorm(dorm.getId());
            dorm.setStudents(students);
            list.add(dorm);
        }

        return list;
    }

    public List<Student> dormStudents(String id) {
        Dorm dorm = new Dorm();
        dorm = dormRepository.findDormById(id);
        return dorm.getStudents();
    }

    public ResultDto<String> dormDelete(String id) {
        Dorm dorm = dormRepository.findDormById(id);
        if(dorm != null) {
            List<Student> students = dorm.getStudents();
            if (students != null && students.size() == 0) {
                try {
                    dormRepository.delete(dorm);
                    logger.info("dorm deleted successfully");
                    return ResultDtoFactory.toAck("S");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    return ResultDtoFactory.toAck("F","删除失败");
                }
            }
            else if (students.size() > 0){
                return ResultDtoFactory.toAck("F","寝室仍有学生居住");
            }


        }
        return ResultDtoFactory.toAck("F","删除失败");

    }

    public List<Dorm> availableDormList() {
        Iterable<Dorm> dorms = dormRepository.findAll();
        List<Dorm> list = new ArrayList<>();

        for (Dorm dorm:dorms) {
            List<Student> students = studentRepository.findStudentsByDorm(dorm.getId());
            dorm.setStudents(students);
            if(dorm.getRemain() > 0)
                list.add(dorm);
        }

        return list;
    }

    public ResultDto<String> dormaAdd(Dorm dorm) {
        Dorm dorm1 = dormRepository.findDormById(dorm.getId());
        if(dorm1 == null)
            return ResultDtoFactory.toAck("F","该寝室已经存在");
        try {
            dormRepository.save(dorm);
            return ResultDtoFactory.toAck("","该寝室已经存在");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResultDtoFactory.toAck("F","添加失败");
        }
    }
}
