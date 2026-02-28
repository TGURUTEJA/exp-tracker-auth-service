package com.Auth_service.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Auth_service.Entity.UserOtp;


@Repository
public interface UserOtpRepository extends CrudRepository<UserOtp, Long> {
    // Additional query methods can be defined here if needed
}
