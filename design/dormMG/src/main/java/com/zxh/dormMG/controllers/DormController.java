package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.DormService;
import com.zxh.dormMG.Service.NoticeService;
import com.zxh.dormMG.domain.Dorm;
import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api(tags = {"Dorm"}, description = "the Dorm API")
@RestController
@RequestMapping(value = "/dorm")
public class DormController {

    @Autowired
    private DormService dormService;

    @RequestMapping(value = "/dormList", method = RequestMethod.GET)
    @ResponseBody
    public DataTableDto dormList() {
        return new DataTableDto<>(dormService.dormList());
    }

    @RequestMapping(value = "/availableDormList", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<List<Dorm>> availableDormList() {
        return ResultDtoFactory.toAck("S",dormService.availableDormList());
    }

    @ApiOperation(value = "get dorm student")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 201, message = "Failed in getting") })
    @RequestMapping(value = "/dormStudents", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<List<Student>> dormStudents(@RequestParam("id") String id) {
        return ResultDtoFactory.toAck("S",dormService.dormStudents(id));
    }


    @RequestMapping(value = "/dormDelete", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> dormDelete(@RequestParam("id") String id) {
        return dormService.dormDelete(id);
    }
}
