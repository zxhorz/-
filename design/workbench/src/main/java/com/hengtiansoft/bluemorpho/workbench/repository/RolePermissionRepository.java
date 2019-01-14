package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.RolePermission;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 13, 2018 2:53:38 PM
 */
@Repository
public interface RolePermissionRepository extends CrudRepository<RolePermission, String> {

	@Query("select p from RolePermission p where p.roleId = :query")
	public List<RolePermission> findAllByRoleId(@Param("query") String roleId);
	
}

