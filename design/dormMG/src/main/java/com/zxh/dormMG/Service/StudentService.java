package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.DormRepository;
import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.domain.Dorm;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.utils.FileUploadUtils;
import com.zxh.dormMG.utils.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.util.*;

@Service
public class StudentService {
    private static final Logger logger = Logger.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DormRepository dormRepository;

    public List<Student> studentList() {
        Iterable<Student> students = studentRepository.findAll();
        List<Student> list = new ArrayList<>();
        for (Student student : students) {
            list.add(student);
        }

        return list;
    }

    public File getUpload(HttpServletRequest request) throws Exception {
        // Servlet3.0方式上传文件
        logger.info("destination");
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            logger.info(part.getName());

            if (part.getContentType() != null) {  // 忽略路径字段,只处理文件类型
                File f = new File(FileUploadUtils.getFileName(part.getHeader("content-disposition")));
                String extension = FileUtils.getFileName(f.getName());
                if (!extension.equals("Spreadsheet"))
                    throw new Exception("不支持非excel文件上传");
                if (!FileUploadUtils.write(part.getInputStream(), f)) {
                    logger.info("failed to upload");
                    throw new Exception("文件上传失败");
                }
                return f;
            }
        }
        return null;
    }

    public ResultDto<String> studentAdd(Student student) {
        String id = student.getId();
        String dormId = student.getDorm();

        Student student1 = studentRepository.findStudentById(id);
        if (student1 != null)
            return ResultDtoFactory.toAck("F", "已存在该学号的学生");

        Dorm dorm = dormRepository.findDormById(dormId);
        List<Student> students = studentRepository.findStudentsByDorm(dorm.getId());
        dorm.setStudents(students);

        if (dorm.getRemain() == 0)
            return ResultDtoFactory.toAck("F", dorm.getId() + "寝室已满");
        else {
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
            return ResultDtoFactory.toAck("F", "添加失败");
        }
    }

    public boolean studentDelete(String id) {
        try {
            Student student = new Student(id);
            studentRepository.delete(student);
            logger.info("student deleted successfully");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
