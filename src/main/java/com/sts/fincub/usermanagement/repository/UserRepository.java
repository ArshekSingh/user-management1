package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT pkg_user_management.fn_get_user_id(:orgId,:userType) USER_ID FROM DUAL",nativeQuery = true)
    String getGeneratedUserEmployeeId(Long orgId,String userType);

    Optional<User> findByName(String userName);

    Optional<User> findByUserId(String userId);


}
