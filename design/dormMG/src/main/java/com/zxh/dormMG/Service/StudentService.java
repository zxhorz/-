package com.zxh.dormMG.Service;

import com.zxh.dormMG.Domain.Dorm;
import com.zxh.dormMG.Domain.Student;
import com.zxh.dormMG.Domain.User;
import com.zxh.dormMG.Repository.DormRepository;
import com.zxh.dormMG.Repository.RoleRepository;
import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.Repository.UserRepository;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.utils.*;
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
    private static final String DOWNLOAD_PATH = "download/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DormService dormService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DormRepository dormRepository;

    @Autowired
    private LoginService loginService;

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
            return ResultDtoFactory.toAck("F", "已存在学号为" + student.getId() + "的学生");

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
            loginService.addUser(student.getId(),PasswordUtil.MD5(student.getId()));
            loginService.addRole(student.getId(),"user");
            studentRepository.save(student);
            //添加对应账号


            return ResultDtoFactory.toAck("S");
        } catch (Exception e) {
            return ResultDtoFactory.toAck("F", "添加失败");
        }
    }

    public boolean studentDelete(String id) {
        try {
            User user = userRepository.findUserByName(id);
            userRepository.delete(user);
            Student student = new Student(id);
            studentRepository.delete(student);

            logger.info("student deleted successfully");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public ResultDto<String> importStudents(File file) {
        FileType fileType = FileUtils.GetFileType(file.getName());
        if (!fileType.equals(FileType.Spreadsheet)) {
            file.delete();
            return ResultDtoFactory.toAck("F", "不支持.xls以外的Excel文件格式");
        } else {
            try {
                List<List<String>> list = DocUtil.readExcel(file);
                Map<Student, String> students = new HashMap<>();
                List<String> attributes = list.get(0);
                int idIndex = attributes.indexOf("学号");
                int nameIndex = attributes.indexOf("姓名");
                int branchIndex = attributes.indexOf("分院");
                int telIndex = attributes.indexOf("电话");
                int emailIndex = attributes.indexOf("邮箱");
                int classIndex = attributes.indexOf("班级");
                if (idIndex == -1 || nameIndex == -1 || branchIndex == -1 || telIndex == -1 || emailIndex == -1 || classIndex == -1)
                    throw new Exception("excel需要有学号、姓名、分院、电话、邮箱、班级六列");

                for (int i = 1; i < list.size(); i++) {
                    List<String> row = list.get(i);
                    Student student = new Student(row.get(idIndex), row.get(nameIndex), row.get(branchIndex), row.get(telIndex), row.get(emailIndex), row.get(classIndex));
                    student = autoImportStudent(student);
                    if (student == null)
                        return ResultDtoFactory.toAck("F", "寝室容量不足");
                    ResultDto<String> result = studentAdd(student);
                    if (result.getMessage().equals("F"))
                        students.put(student, result.getData());
                }

                if (students.size() != 0) {
                    File tempFile = new File(FilePathUtil.createDownloadFile("导入失败名单.xls"));
                    if(tempFile.delete()){
                    if (!tempFile.exists()) {
                        tempFile.createNewFile();
                    }}
                    else{
                        return ResultDtoFactory.toAck("F","文件存储错误");
                    }

                    ArrayList<ArrayList> writeContent = new ArrayList<>();
                    ArrayList<String> titles = new ArrayList<>();
                    titles.add("学号");
                    titles.add("姓名");
                    titles.add("失败原因");
                    writeContent.add(titles);

                    for (Student student : students.keySet()) {
                        ArrayList<String> columns = new ArrayList<>();
                        columns.add(student.getId());
                        columns.add(student.getName());
                        columns.add(students.get(student));
                        writeContent.add(columns);

                    }

                    DocUtil.writeExcel(writeContent,DOWNLOAD_PATH+"导入失败学生名单.xls");

                    file.delete();
                    return ResultDtoFactory.toAck("W", "部分导入成功");
                }

            } catch (Exception e) {
                logger.error(e);
                return ResultDtoFactory.toAck("F", e.getMessage());
            }
        }
        file.delete();
        return ResultDtoFactory.toAck("S", "导入成功");
    }

    public Student autoImportStudent(Student student) {
        List<Dorm> dorms = dormService.availableDormList();
        if (dorms.isEmpty())
            return null;
        float max = -Float.MAX_VALUE;
        String dormId = dorms.get(0).getId();
        for (Dorm dorm : dorms) {
            List<Student> students = dorm.getStudents();
            if (students.isEmpty() && max <= 0 && !dormRepository.findDormById(dormId).getStudents().isEmpty())
                dormId = dorm.getId();
            else {
                for (Student student1 : students) {
                    float distance = EditorDistance.Levenshtein(student1.getStudentClass(), student.getStudentClass());
                    if (distance == 1) {
                        student.setDorm(dorm.getId());
                        return student;
                    } else if (distance > max) {
                        max = distance;
                        dormId = dorm.getId();
                    }
                }
            }

        }

        student.setDorm(dormId);
        return student;
    }
}
