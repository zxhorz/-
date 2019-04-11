package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.ApplicationService;
import com.zxh.dormMG.Service.ApplicationService;
import com.zxh.dormMG.domain.Application;
import com.zxh.dormMG.dto.ApplicationDto;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    //
    @RequestMapping(value = "/applicationList", method = RequestMethod.GET)
    @ResponseBody
    public DataTableDto applicationList() {
        return new DataTableDto<>(applicationService.applicationList());
    }

    //
    @RequestMapping(value = "/applicationAdd", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> applicationAdd(ApplicationDto applicationDto) {
        Application application = new Application(applicationDto, new Date());
        if (applicationService.applicationAdd(application))
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }

    //
    @RequestMapping(value = "/applicationGet", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> applicationGet(@RequestParam("id") String id) {
        Application application = applicationService.applicationGet(id);
        if (application != null)
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }

//    @RequiresRoles("admin")
    @RequestMapping(value = "/applicationDelete", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> applicationDelete(@RequestParam("id") String id) {
        if (applicationService.applicationDelete(id))
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }

//    @RequiresRoles("admin")
    @RequestMapping(value = "/operate", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> operate(@RequestParam("id") String id, @RequestParam("operation") String operation) {
        if (applicationService.operate(id, operation))
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }


}
