package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.Role;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends BaseRepository<Role,Long> {
    @Query("select p.d from role p where p.userId = :userId")
    String findByUserId(String userId);
}