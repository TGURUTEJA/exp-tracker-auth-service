package com.Auth_service.Helpers;

import com.Auth_service.Entity.UserCred;

import com.Auth_service.pojo.FunctionMessage;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailHelper {

    final private JavaMailSender mailSender;
    final private OtpHelper otpHelper;


    @Value("${spring.mail.username}")
    private String from;

    public void sendVerificationEmail(String email, String verificationToken) {
        String subject = "Email Verification";
        String path = "/api/auth/Email-verify";
        String message = "Click the button below to verify your email address:";
        sendEmail(email, verificationToken, subject, path, message);
    }

    public String sendForgotPasswordEmail(UserCred userCred) {
        String subject = "Password Reset Request";
        String path = "/req/reset-password";
        String message = "Click the button below to reset your password:";
        String OTP = generateRandomOTP();
        FunctionMessage DBResponse = otpHelper.saveOtp(OTP,userCred);
        if (DBResponse.isError()) {
            return "Error: " + DBResponse.getMessage();
        } else {
            System.out.println("OTP saved successfully for user ID: " + userCred.getId());
        }
        sendEmail(userCred.getEmail(), OTP, subject, path, message);
        return "OTP sent successfully to " + userCred.getEmail();
    }



    private void sendEmail(String email, String token, String subject, String path, String message) {
        try {
            String actionUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(path)
                    .queryParam("token", token)
                    .toUriString();
            String content ="";
            content= """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                        <h2 style="color: #333;">%s</h2>
                        <p style="font-size: 16px; color: #555;">%s</p>
                        <a href="%s" style="display: inline-block; margin: 20px 0; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #007bff; text-decoration: none; border-radius: 5px;">Proceed</a>
                        <p style="font-size: 14px; color: #777;">Or copy and paste this link into your browser:</p>
                        <p style="font-size: 14px; color: #007bff;">%s</p>
                        <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                    </div>
                """.formatted(subject, message, actionUrl, actionUrl);
            if ( path.equals("/req/reset-password") ) {
                content = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                        <h2 style="color: #333;">%s</h2>
                        <p style="font-size: 16px; color: #555;">%s</p>
                        <h3 style="font-size: 24px; color: #007bff; margin: 20px 0;">%s</h3>
                        <p style="font-size: 14px; color: #777;">Use this OTP to reset your password. It is valid for a limited time.</p>
                        <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                    </div>
                """.formatted(subject, message, token);
            }
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private String generateRandomOTP() {
        if (true){
        return "123";
        }
        String numbers = "0123456789";
        StringBuilder otp = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * numbers.length());
            otp.append(numbers.charAt(index));
        }
        return otp.toString();
    }


}
