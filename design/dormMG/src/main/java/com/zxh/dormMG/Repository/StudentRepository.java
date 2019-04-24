package com.zxh.dormMG.Repository;

import com.zxh.dormMG.Domain.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface StudentRepository extends BaseRepository<Student,Long> {
    @Query("select n from Student n where n.id = :query")
    Student findStudentById(@Param("query") String id);

    @Query("select n from Student n where n.dorm = :query")
    List<Student> findStudentsByDorm(@Param("query") String id);
}