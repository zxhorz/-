package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.AnalysisDependency;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 7:08:26 PM
 */
@Repository
public interface AnalysisDependencyRepository extends CrudRepository<AnalysisDependency, String> {
	
	@Query("select p.relieredId from AnalysisDependency p where p.relierId = :query")
	public List<String> findRelieredIdByRelierId(@Param("query") String relier_id);

	@Query("select p from AnalysisDependency p")
	public List<AnalysisDependency> getAllAnalysisDependencies();

}
