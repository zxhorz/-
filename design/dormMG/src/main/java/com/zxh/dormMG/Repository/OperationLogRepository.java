package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.OperationLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 15, 2018 11:36:38 AM
 */
@Repository
public interface OperationLogRepository extends CrudRepository<OperationLog, String> {
	
}
