package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.NoticeService;
import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.dto.DataTableDto;
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
    @RequestMapping(value = "/noticeSave", method = RequestMethod.POST)
    @ResponseBody
    public DataTableDto noticeSave(@RequestParam("title") String title,@RequestParam("content") String content) {
        Notice notice = new Notice(title,new Date(),content);
        noticeService.noticeSave(notice);
        return new DataTableDto<>(noticeService.noticeList());
    }


}
