package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.NoticeService;
import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    //
    @RequestMapping(value = "/noticeList", method = RequestMethod.GET)
    @ResponseBody
    public DataTableDto noticeList() {
        return new DataTableDto<>(noticeService.noticeList());
    }

    //
    @RequiresRoles("admin")
    @RequestMapping(value = "/noticeSave", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> noticeSave(@RequestParam("title") String title, @RequestParam("content") String content) {
        Notice notice = new Notice(title,new Date(),content);
        if(noticeService.noticeSave(notice))
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }

    //
    @RequestMapping(value = "/noticeGet", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> noticeGet(@RequestParam("id") String id) {
        Notice notice = noticeService.noticeGet(id);
        if(notice != null)
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }

    @RequiresRoles("admin")
    @RequestMapping(value = "/noticeDelete", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> noticeDelete(@RequestParam("id") String id) {
        if(noticeService.noticeDelete(id))
            return ResultDtoFactory.toAck("S");
        return ResultDtoFactory.toAck("F");
    }


}
