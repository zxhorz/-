package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.BusinessDomainsCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:28:30 AM
 */
@Repository
public interface BusinessDomainsCorpusRepository extends CrudRepository<BusinessDomainsCorpus, Integer> {

	@Query(value = "select p from BusinessDomainsCorpus p where p.domainName = :name")
	BusinessDomainsCorpus findByName(@Param("name") String name);
	
}
