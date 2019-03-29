package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.MyInfoService;
import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.dto.StudentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/myInfo")
public class MyInfoController {

    @Autowired
    private MyInfoService myInfoService;

    @RequestMapping(value = "/myInfoSave", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> myInfoSave(StudentDto studentDto) {
        return (myInfoService.myInfoSave(studentDto));
    }

    //
    @RequestMapping(value = "/myInfoGet", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<StudentDto> myInfoGet(@RequestParam("id") String id) {
        Student student = myInfoService.myInfoGet(id);
        if (student != null)
            return ResultDtoFactory.toAck("S", new StudentDto(student));
        else
            return ResultDtoFactory.toAck("F");
    }

}
