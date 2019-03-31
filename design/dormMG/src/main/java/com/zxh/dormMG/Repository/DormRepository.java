package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.Dorm;
import com.zxh.dormMG.domain.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
public interface DormRepository extends BaseRepository<Dorm,Long> {
    @Query("select n from Dorm n where n.id = :query")
    Dorm findDormById(@Param("query") String id);
}