package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.Role;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 13, 2018 3:07:22 PM
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, String> {

	@Query("select p.name from Role p where p.id = :query")
	public String findNameById(@Param("query") String roleId);
	
}

