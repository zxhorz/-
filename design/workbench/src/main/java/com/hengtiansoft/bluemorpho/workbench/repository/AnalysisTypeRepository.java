package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.AnalysisType;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 7:09:19 PM
 */
@Repository
public interface AnalysisTypeRepository extends CrudRepository<AnalysisType, String> {

	@Query("select p.id from AnalysisType p where p.analysisName = :query")
	public String findIdByName(@Param("query") String analysisName);
	
	@Query("select p from AnalysisType p")
	public List<AnalysisType> findAllAnalysisTypes();

	@Query("select p.analysisName from AnalysisType p where p.id = :id")
	public String findAnalysisNameById(@Param("id") String id);

	@Query("select p from AnalysisType p where p.analysisName = :name")
	public AnalysisType findByName(@Param("name") String name);
	
}
