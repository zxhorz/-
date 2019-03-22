package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.JobStatus;

@Transactional
@Repository
public interface JobStatusRepository extends PagingAndSortingRepository<JobStatus, String>{

	@Query(value = "select * from job_status p where p.projectId = :projectId and p.analysis_type_id = :analysisTypeId order by p.id desc limit 1",nativeQuery = true)
	JobStatus findByProjectIdAndAnalysisTypeId(@Param("projectId") String projectId, @Param("analysisTypeId") String analysisTypeId);
	
	@Query(value = "select * from job_status p where p.projectId = :projectId and p.analysis_type_id = :analysisTypeId order by p.id",nativeQuery = true)
	List<JobStatus> findAllByProjectIdAndAnalysisTypeId(@Param("projectId") String projectId, @Param("analysisTypeId") String analysisTypeId);

	@Query(value = "select p from JobStatus p where p.projectId = :projectId and p.analysisTypeId = :analysisTypeId and p.codeVersion = :codeVersion order by p.startTime")
	List<JobStatus> findByProjectIdAndAnalysisIdAndCodeVersion(@Param("projectId") String projectId, @Param("analysisTypeId") String analysisTypeId, @Param("codeVersion") String codeVersion);

	@Query(value = "select p from JobStatus p where p.jobName = :jobName")
	JobStatus findbyName(@Param("jobName") String jobName);

	@Query(value = "select p from JobStatus p where p.projectId = :projectId")
	List<JobStatus> findByProjectId(@Param("projectId") String projectId);
	
	@Query(value = "select p from JobStatus p where p.projectId = :projectId and (p.status = 'NS' or p.status = 'P')")
	List<JobStatus> findWaitingOrRunningJobForProject(@Param("projectId") String projectId);
	
	@Query(value = "select p.startTime from JobStatus p where p.jobName = :jobName")
	Date findStartTimeByJobName(@Param("jobName") String jobName);
	
	@Query(value = "select p from JobStatus p where p.projectId = :projectId and p.status <> 'NS' and p.status <> 'P'")
	List<JobStatus> findAllExceptWaitingOrRunningJobs(@Param("projectId") String projectId);
	
	@Query(value = "select * from job_status p where p.projectId = :projectId and p.analysis_type_id in :analysisTypeIds and p.code_version = (select max(p1.code_version) from job_status p1 where p1.projectId = :projectId) order by p.start_time", nativeQuery = true)
	List<JobStatus> findLatestJobs(@Param("projectId") String projectId, @Param("analysisTypeIds") List<String> analysisTypeIds);

	@Modifying
	@Query(value = "delete from JobStatus j where j.projectId = :projectId")
    void deleteByProjectId(@Param("projectId") String projectId);

}
