package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.NoticeService;
import com.zxh.dormMG.domain.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    //post登录
    @RequestMapping(value = "/noticeList", method = RequestMethod.POST)
    @ResponseBody
    public List<Notice> notice() {
        return noticeService.noticeList();
    }

}
