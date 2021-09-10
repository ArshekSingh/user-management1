package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.entity.UserSession;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConfigurationProperties(prefix = "redis")
public interface UserRedisRepository extends CrudRepository<UserSession,String> {

}
