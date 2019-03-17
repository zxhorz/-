package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.NoticeRepository;
import com.zxh.dormMG.domain.Notice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeService {

    private static final Logger logger = Logger.getLogger(NoticeService.class);

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

    public boolean noticeSave(Notice notice) {
        try {
            noticeRepository.save(notice);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    public Notice noticeGet(String id) {
        return noticeRepository.findNoticeById(id);
    }
}
