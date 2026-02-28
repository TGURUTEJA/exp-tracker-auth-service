package com.Auth_service.DBHandler;

import com.Auth_service.Entity.UserOtp;
import com.Auth_service.Repository.UserOtpRepository;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserOtpDBHandler {

    @Autowired
    UserOtpRepository userOtpRepository;

    public UserOtp saveOtp(UserOtp userOtp) {
        try {
            return userOtpRepository.save(userOtp);
        } catch (Exception e) {
            // Ignore if no existing OTP
            System.out.println("Error saving OTP: " + e.getMessage() + userOtp.toString());
            return null;
        }

    }
    public UserOtp findOtpById(Long id) {
        try {
            return userOtpRepository.findById(id).orElse(null);
        } catch (Exception e) {
            // Ignore if no existing OTP
            return null;
        }
    }

}
