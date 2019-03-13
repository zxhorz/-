package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface NoticeRepository extends BaseRepository<Notice,Long> {
    
}