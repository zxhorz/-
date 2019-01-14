package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.OrganizationsCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:29:25 AM
 */
@Repository
public interface OrganizationsCorpusRepository extends CrudRepository<OrganizationsCorpus, Integer> {

	@Query(value = "select p from OrganizationsCorpus p where p.organizationName = :name")
	OrganizationsCorpus findByName(@Param("name") String name);
	
}
