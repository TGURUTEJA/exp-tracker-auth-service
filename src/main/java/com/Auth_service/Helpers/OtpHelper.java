package com.Auth_service.Helpers;


import com.Auth_service.DBHandler.UserOtpDBHandler;
import com.Auth_service.Entity.UserCred;
import com.Auth_service.Entity.UserOtp;

import com.Auth_service.pojo.FunctionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OtpHelper {

    private final UserOtpDBHandler userOtpDBHandler;

    public FunctionMessage saveOtp(String otp, UserCred userCred) {
        UserOtp userOtp = userOtpDBHandler.findOtpById(userCred.getId());
        LocalDateTime now = LocalDateTime.now();
        UserOtp newUserOtp = new UserOtp();
        if (userOtp != null) {
            if (userOtp.getCount() <= 5) {
                updateOtpData(userOtp, otp, now, userOtp.getCount() + 1);
            } else  {
                if (userOtp.getResetTime().isBefore(now.minusMinutes(30))) {
                    updateOtpData(userOtp, otp, now, 1);
                } else {
                    return new FunctionMessage("OTP limit exceeded please try again after sometime", true);
                }
            }
            newUserOtp = userOtp;
        } else {
            newUserOtp.setOtp(otp);
            newUserOtp.setCount(1);
            newUserOtp.setExpireTime(now.plusMinutes(5));
            newUserOtp.setResetTime(now);
            newUserOtp.setUserCred(userCred);
        }
        UserOtp savedOtp = userOtpDBHandler.saveOtp(newUserOtp);
        if (savedOtp != null) {
            return new FunctionMessage("OTP sent successfully", false);
        } else {
            return new FunctionMessage("Error while generating OTP ", true);
        }
    }

    public FunctionMessage verifyOtp(String otp, Long userId) {
        UserOtp userOtp = userOtpDBHandler.findOtpById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (userOtp == null) {
            return new FunctionMessage("No OTP found please generate OTP", true);
        }

        if (userOtp.getExpireTime().isAfter(now)) {
            if (userOtp.getOtp().equals(otp)) {
                if (userOtp.getPasswordResetTime() != null && userOtp.getPasswordResetTime().isAfter(LocalDateTime.now().minusHours(24))) {
                    return new FunctionMessage("Password already reset within the last 24 hours for user id " + userId,true);
                }
                userOtp.setPasswordResetTime(now);
                UserOtp savedOtp = userOtpDBHandler.saveOtp(userOtp);
                if (savedOtp == null) {
                    return new FunctionMessage("Error while verifying OTP", true);
                }
                return new FunctionMessage("OTP verification successful ", false);
            } else {
                return new FunctionMessage("Invalid OTP", true);
            }
        } else {
            return new FunctionMessage("OTP is Expired please try again", true);
        }
    }

    private void updateOtpData(UserOtp userOtp, String otp, LocalDateTime now, int count) {
        userOtp.setOtp(otp);
        userOtp.setCount(count);
        userOtp.setExpireTime(now.plusMinutes(5));
        if (count == 1) {
            userOtp.setResetTime(now);
        }
    }

}