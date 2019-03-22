package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.SystemsCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:30:45 AM
 */
@Repository
public interface SystemsCorpusRepository extends CrudRepository<SystemsCorpus, Integer> {

	@Query(value = "select p from SystemsCorpus p where p.systemName = :name")
	SystemsCorpus findByName(@Param("name") String name);
	
}
