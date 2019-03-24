package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.DormService;
import com.zxh.dormMG.Service.NoticeService;
import com.zxh.dormMG.domain.Dorm;
import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

}
