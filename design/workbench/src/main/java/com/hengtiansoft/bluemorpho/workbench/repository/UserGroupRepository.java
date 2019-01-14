package com.hengtiansoft.bluemorpho.workbench.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.UserGroup;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 15, 2018 4:42:52 PM
 */
@Repository
public interface UserGroupRepository extends CrudRepository<UserGroup, String> {

}

