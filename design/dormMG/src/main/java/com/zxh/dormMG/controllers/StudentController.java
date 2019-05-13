package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Domain.Student;
import com.zxh.dormMG.Service.StudentService;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.utils.FilePathUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/studentAdd", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> studentAdd(Student student) {
        return (studentService.studentAdd(student));
    }

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/studentDelete", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> studentDelete(@RequestParam("id") String id) {
        if (studentService.studentDelete(id))
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/importStudents", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> importStudents(@RequestParam("file")MultipartFile file) {
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
        return ResultDtoFactory.toAck("F","导入失败");
    }

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/downloadFailedImport", method = RequestMethod.POST)
    @ResponseBody
    public void downloadFailedImport(HttpServletResponse response) {
        try {
            File file = FilePathUtil.getDownloadFilePath("导入失败学生名单.xls");
            FilePathUtil.download(file, response);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
