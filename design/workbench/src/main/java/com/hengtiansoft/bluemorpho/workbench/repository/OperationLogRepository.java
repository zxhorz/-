package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.OperationLog;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 15, 2018 11:36:38 AM
 */
@Repository
public interface OperationLogRepository extends CrudRepository<OperationLog, String> {
	
}
