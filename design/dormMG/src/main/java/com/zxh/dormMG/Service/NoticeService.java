package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.NoticeRepository;
import com.zxh.dormMG.domain.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    public List<Notice> noticeList() {
        Iterable<Notice> notices = noticeRepository.findAll();
        List<Notice> list = new ArrayList<>();
        for (Notice notice:notices) {
            list.add(notice);
        }
        return list;
    }

    public void noticeSave(Notice notice) {
        noticeRepository.save(notice);
    }


}
