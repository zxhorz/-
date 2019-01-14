package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.Group;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 15, 2018 4:40:47 PM
 */
@Repository
public interface GroupRepository extends CrudRepository<Group, String> {

}

