package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.Notice;
import com.zxh.dormMG.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface NoticeRepository extends BaseRepository<Notice,Long> {
    
}