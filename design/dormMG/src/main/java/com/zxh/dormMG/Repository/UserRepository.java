package com.zxh.dormMG.Repository;

import com.zxh.dormMG.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends BaseRepository<User,Long>{
    @Query("select p from User p where p.name = :query")
    User findUserByName(@Param("query") String name);

    @Query("select p from User p where p.activationCode = :query")
    public User findUserByActivationCode(@Param("query") String activationCode);

}