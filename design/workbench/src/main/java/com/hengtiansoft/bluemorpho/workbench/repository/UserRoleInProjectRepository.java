package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hengtiansoft.bluemorpho.workbench.domain.UserRoleInProject;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 13, 2018 2:03:53 PM
 */
@Transactional
@Repository
public interface UserRoleInProjectRepository extends CrudRepository<UserRoleInProject, String> {

	@Query("select p from UserRoleInProject p where p.userId = :query")
	public List<UserRoleInProject> findAllByUserId(@Param("query") String userId);

	@Query("select p.roleId from UserRoleInProject p where p.userId = :userId and p.projectId =:projectId")
	public String findByUserIdAndProjectId(@Param("userId") String userId, @Param("projectId") String projectId);

	@Modifying
	@Query(value = "delete from UserRoleInProject j where j.projectId = :projectId")
	public void deleteByProjectId(@Param("projectId") String projectId);
	
}
