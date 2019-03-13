package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.OperationLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface OperationLogRepository extends CrudRepository<OperationLog, String> {
	
}
