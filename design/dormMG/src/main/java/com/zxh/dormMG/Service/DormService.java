package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.DormRepository;
import com.zxh.dormMG.Repository.StudentRepository;
import com.zxh.dormMG.Domain.Dorm;
import com.zxh.dormMG.Domain.Student;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.utils.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DormService {
    private static final Logger logger = Logger.getLogger(DormService.class);
    private static final String DOWNLOAD_PATH = "download/";
    @Autowired
    private DormRepository dormRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Dorm> dormList() {
        Iterable<Dorm> dorms = dormRepository.findAll();
        List<Dorm> list = new ArrayList<>();
        for (Dorm dorm : dorms) {
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
        if (dorm != null) {
            List<Student> students = studentRepository.findStudentsByDorm(dorm.getId());
            dorm.setStudents(students);
            if (students != null && students.size() == 0) {
                try {
                    dormRepository.delete(dorm);
                    logger.info("dorm deleted successfully");
                    return ResultDtoFactory.toAck("S");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    return ResultDtoFactory.toAck("F", "删除失败");
                }
            } else if (students.size() > 0) {
                return ResultDtoFactory.toAck("F", "寝室仍有学生居住");
            }


        }
        return ResultDtoFactory.toAck("F", "删除失败");

    }

    public List<Dorm> availableDormList() {
        Iterable<Dorm> dorms = dormRepository.findAll();
        List<Dorm> list = new ArrayList<>();

        for (Dorm dorm : dorms) {
            List<Student> students = studentRepository.findStudentsByDorm(dorm.getId());
            dorm.setStudents(students);
            if (dorm.getRemain() > 0)
                list.add(dorm);
        }

        return list;
    }

    public ResultDto<String> dormAdd(Dorm dorm) {
        Dorm dorm1 = dormRepository.findDormById(dorm.getId());
        if (dorm1 != null)
            return ResultDtoFactory.toAck("F", dorm.getId() + "寝室已经存在");
        try {
            dormRepository.save(dorm);
            return ResultDtoFactory.toAck("S", dorm.getId() + "添加成功");

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultDtoFactory.toAck("F", "添加失败");
        }
    }

    public ResultDto<String> importDorms(File file) {
        FileType fileType = FileUtils.GetFileType(file.getName());
        if (!fileType.equals(FileType.Spreadsheet)) {
            file.delete();
            return ResultDtoFactory.toAck("F", "不支持.xls以外的Excel文件格式");
        } else {
            try {
                List<List<String>> list = DocUtil.readExcel(file);
                Map<Dorm, String> dorms = new HashMap<>();
                List<String> attributes = list.get(0);
                int idIndex = attributes.indexOf("寝室");
                int volumeIndex = attributes.indexOf("容量");
                if (idIndex == -1 || volumeIndex == -1)
                    throw new Exception("excel需要有寝室、容量两列");

                for (int i = 1; i < list.size(); i++) {
                    List<String> row = list.get(i);
                    Dorm dorm = new Dorm(row.get(idIndex), Integer.valueOf(row.get(volumeIndex)));

                    ResultDto<String> result = dormAdd(dorm);
                    if (result.getMessage().equals("F"))
                        dorms.put(dorm, result.getData());
                }

                if (dorms.size() != 0) {
                    File tempFile = new File(FilePathUtil.createDownloadFile("导入失败寝室列表.xls"));
                    tempFile.delete();
                    if (!tempFile.exists()) {
                        tempFile.createNewFile();
                    }

                    ArrayList<ArrayList> writeContent = new ArrayList<>();
                    ArrayList<String> titles = new ArrayList<>();
                    titles.add("寝室");
                    titles.add("失败原因");
                    writeContent.add(titles);

                    for (Dorm dorm : dorms.keySet()) {
                        ArrayList<String> columns = new ArrayList<>();
                        columns.add(dorm.getId());
                        columns.add(dorms.get(dorm));
                        writeContent.add(columns);

                    }

                    DocUtil.writeExcel(writeContent, DOWNLOAD_PATH + "导入失败寝室列表.xls");

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
}
