package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.StudentService;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/student")
public class StudentController {

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

}
