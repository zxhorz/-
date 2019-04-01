package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.StudentService;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.utils.FilePathUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping(value = "/student")
public class StudentController {
    private static final Logger logger = Logger.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @RequestMapping(value = "/studentList", method = RequestMethod.GET)
    @ResponseBody
    public DataTableDto studentList() {
        return new DataTableDto<>(studentService.studentList());
    }

    @RequestMapping(value = "/studentAdd", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> studentAdd(Student student) {
        return (studentService.studentAdd(student));
    }

    @RequestMapping(value = "/studentDelete", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> studentDelete(@RequestParam("id") String id) {
        if (studentService.studentDelete(id))
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }

    @RequestMapping(value = "/importStudents", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> importStudents(@RequestParam("file")MultipartFile file) {
//        return (studentService.studentAdd(student));
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String path = FilePathUtil.createUploadFile(fileName);
            File tempFile =new File(path);
            //写文件到本地
            try {
                file.transferTo(tempFile);
                return (studentService.importStudents(tempFile));
            } catch (IOException e) {
                logger.error(e);
                return ResultDtoFactory.toAck("F","导入失败");
            }
        }


    }
}
