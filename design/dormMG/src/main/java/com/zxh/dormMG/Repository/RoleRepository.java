package com.zxh.dormMG.Repository;

import com.zxh.dormMG.Domain.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface RoleRepository extends BaseRepository<Role,Long> {
    @Query("select n from Role n where n.roleName = :query")
    public Role findRoleByName(@Param("query") String name);
}