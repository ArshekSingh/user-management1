package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.UserSession;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisRepository extends CrudRepository<UserSession,String> {

}
