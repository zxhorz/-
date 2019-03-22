package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.ProjectsCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:30:00 AM
 */
@Repository
public interface ProjectsCorpusRepository extends CrudRepository<ProjectsCorpus, Integer> {

}
