package com.zxh.dormMG.Repository;

import com.zxh.dormMG.Domain.Notice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface NoticeRepository extends BaseRepository<Notice,Long> {
    @Query("select n from Notice n where n.id = :query")
    Notice findNoticeById(@Param("query") String id);
}