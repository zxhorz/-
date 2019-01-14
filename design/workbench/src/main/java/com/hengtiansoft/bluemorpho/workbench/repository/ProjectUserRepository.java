package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.hengtiansoft.bluemorpho.workbench.domain.ProjectUser;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date May 20, 2018
 */
public interface ProjectUserRepository extends
		CrudRepository<ProjectUser, String> {

	@Query("select p from ProjectUser p where p.projectId = :projectId and p.status = 1")
	public List<ProjectUser> findAllByProjectId(
			@Param("projectId") String projectId);

	@Query("select p from ProjectUser p where p.projectId = :projectId and userId= :userId")
	public ProjectUser findAllByProjectIdAdnUserId(
			@Param("projectId") String projectId, @Param("userId") String userId);

	@Modifying
	@Query("update ProjectUser p set p.status=:status where p.projectId = :projectId and p.userId = :userId")
	public void updateStatus(@Param("projectId") String projectId,
			@Param("userId") String userId, @Param("status") int status);

}
