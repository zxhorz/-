package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface RoleRepository extends BaseRepository<Role,Long> {
}