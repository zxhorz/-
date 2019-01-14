package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

	@Query("select p from User p where p.username = :query")
	public User findUserByName(@Param("query") String username);

	@Query("select p.id from User p where p.username = :query")
	public String findUserIdByName(@Param("query") String username);
	
	@Query("select p from User p where p.activationCode = :query")
	public User findUserByActivationCode(@Param("query") String activationCode);
	
}
