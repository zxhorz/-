package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.NoticeRepository;
import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.UserDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            logger.error(e.getMessage());
            return false;
        }
    }


    public Notice noticeGet(String id) {
        return noticeRepository.findNoticeById(id);
    }

    public boolean noticeDelete(String id) {
        try{
            Notice notice = new Notice(id);
            noticeRepository.delete(notice);
            logger.info("notice deleted successfully");
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    public List<Notice> noticePreview() {
        List<Notice> list = new ArrayList<>();
        List<Notice> result = new ArrayList<>();
        Iterable<Notice> applications;
        Sort sort = new Sort(Sort.Direction.DESC, "date");
        applications = noticeRepository.findAll(sort);

        for (Notice application : applications) {
            list.add(application);
        }
        if (list.size() == 0)
            return list;
        for (int i = 0; i < Math.min(4,list.size()); i++) {
            result.add(list.get(i));
        }
        return result;
    }
}
