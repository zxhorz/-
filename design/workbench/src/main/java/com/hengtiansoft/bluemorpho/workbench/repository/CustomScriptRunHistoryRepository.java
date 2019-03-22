package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.hengtiansoft.bluemorpho.workbench.domain.CustomScriptRunHistory;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 25, 2018 2:27:34 PM
 */
@Transactional
public interface CustomScriptRunHistoryRepository extends PagingAndSortingRepository<CustomScriptRunHistory, String> {

   @Query(value = "select c from CustomScriptRunHistory c where c.basedProjectId = :projectId")
   List<CustomScriptRunHistory> findByProjectId(@Param("projectId") String projectId);
   
   @Query(value = "select c from CustomScriptRunHistory c where c.basedProjectId in (:projectId)")
   List<CustomScriptRunHistory> findByProjectId(@Param("projectId") List<String> projectId);
   
   @Query(value = "select c from CustomScriptRunHistory c where c.runId = :runId")
   CustomScriptRunHistory findByRunId(@Param("runId") String runId);
   
   @Modifying
   @Query(value = "delete from CustomScriptRunHistory c where c.runId = :runId")
   void deleteByRunId(@Param("runId") String runId);
   
   @Query(value = "select c.runId from CustomScriptRunHistory c "
           + "where c.id = "
           + "(select max(c.id) as max from c where c.basedProjectId = :projectId)")
   String findByLastScript(@Param("projectId") String projectId);
   
   @Modifying
   @Query(value = "delete from CustomScriptRunHistory c where c.basedProjectId = :projectId")
   void deleteByProjectId(@Param("projectId") String projectId);

}
