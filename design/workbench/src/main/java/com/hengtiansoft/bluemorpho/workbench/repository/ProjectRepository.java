package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.Project;
/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 15, 2018 4:41:31 PM
 */
@Repository
public interface ProjectRepository extends CrudRepository<Project, String> {

	@Query("select p from User u ,UserRoleInProject m ,Project p where u.id = m.userId and m.projectId = p.id and u.username = :userName")
	public List<Project> findAllProjectByUserName(@Param("userName") String userName);

	@Query("select p from Project p where p.name = :projectName")
	public Project findByProjectName(@Param("projectName") String projectName);	
	
    @Query("select p from Project p where p.id = :projectId")
    public Project findByProjectId(@Param("projectId") String projectId);
    
    @Query("select p.path from Project p where p.id = :projectId")
    public String findPathByProjectId(@Param("projectId") String projectId);

    @Query("select p from Project p where p.createrId = :createrId")
	public List<Project> findAllByCreateId(@Param("createrId") String createrId); 
}

