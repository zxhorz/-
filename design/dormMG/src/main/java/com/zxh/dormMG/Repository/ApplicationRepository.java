package com.zxh.dormMG.Repository;

import com.zxh.dormMG.Domain.Application;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface ApplicationRepository extends BaseRepository<Application,Long> {
    @Query("select n from Application n where n.id = :query")
    Application findApplicationById(@Param("query") String id);

    @Query("select n from Application n where n.studentId = :query")
    List<Application> findApplicationByName(@Param("query") String studentId);

    @Query("select n from Application n where n.studentId = :query order by n.date desc")
    List<Application> findApplicationByNameOrderByDate(@Param("query") String studentId);
}