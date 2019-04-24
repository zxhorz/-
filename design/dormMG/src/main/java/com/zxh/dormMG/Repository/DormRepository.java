package com.zxh.dormMG.Repository;

import com.zxh.dormMG.Domain.Dorm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

@Transactional
public interface DormRepository extends BaseRepository<Dorm,Long> {
    @Query("select n from Dorm n where n.id = :query")
    Dorm findDormById(@Param("query") String id);
}