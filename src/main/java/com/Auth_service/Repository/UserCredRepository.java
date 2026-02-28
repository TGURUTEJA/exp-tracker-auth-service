package com.Auth_service.Repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Auth_service.Entity.UserCred;

import java.util.Optional;


@Repository
public interface UserCredRepository extends CrudRepository<UserCred, Long> {
    // Additional query methods can be defined here if needed
    Optional<UserCred> findByUserName(String userName);
    // // Find user credentials by email
    Optional<UserCred> findByEmail(String email);

}
