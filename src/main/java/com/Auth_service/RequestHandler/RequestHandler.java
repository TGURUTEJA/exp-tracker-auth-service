package com.Auth_service.RequestHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.Auth_service.DBHandler.UserCredDBHandler;
import com.Auth_service.Entity.UserCred;
import com.Auth_service.Helpers.EmailHelper;
import com.Auth_service.Helpers.OtpHelper;
import com.Auth_service.pojo.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final UserCredDBHandler userCredDBHandler;
    private final EmailHelper helper;
    private final OtpHelper otpHelper;
    private final RequestHelper requestHelper;

    public ResponseEntity<AuthResponse> loginHandler(AuthRequest request) {
        List<CheckMessage> erros= requestHelper.getLoginCheckMessages(request);
        if(!erros.isEmpty()){
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("not a valid request", erros));
        }

        UserDBResponse response = lookupUserByUsernameOrEmail(request.getUserName(), request.getEmail());
        UserCred userCred = response.getUserData();
        if (response.isError()) {
            if (null!=userCred){
                return ResponseEntity.badRequest().body(requestHelper.authErrorResponse(response.getMessage(), null));
            }
            return ResponseEntity.internalServerError().body(requestHelper.authErrorResponse(response.getMessage(), null));
        }

        if (!userCred.isVerified()) {
            return ResponseEntity.status(401).body(requestHelper.authErrorResponse("Account is not verified", null));        }
        if (!request.getPassword().equals(userCred.getPassword())) {
            return ResponseEntity.status(401).body(requestHelper.authErrorResponse("Incorrect password", null));
        }

        return requestHelper.responseWithJWT(requestHelper.authSuccessResponse("Login successful", userCred.getId()),userCred.getRole(),false);
    }

    public ResponseEntity<AuthResponse> RegisterHandler(RegisterRequest request) {
        List<CheckMessage> errorData = requestHelper.getRegisterCheckMessages(request);
        if (!errorData.isEmpty()) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("not a valid request", errorData));
        }

        AuthResponse dbResponse = userCredDBHandler.registerDBCheckAndSave(request);
        if (dbResponse.isError()) {
            return ResponseEntity.badRequest().body(dbResponse);
        }
        return ResponseEntity.ok(dbResponse);
    }


    public ResponseEntity<AuthResponse> forgotPasswordHandler(String email, String username) {
        if (requestHelper.isBlank(email) && requestHelper.isBlank(username)) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("Email or UserName must be provided", null));
        }
        UserDBResponse res =lookupUserByUsernameOrEmail(username, email);
        if (res == null || res.isError() || null==res.getUserData()) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("Can not find email with UserName; please provide email or try again",null));
        }
        if (!res.getUserData().isVerified()){
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("account is not verified; please verify first",null));
        }
        String message = helper.sendForgotPasswordEmail(res.getUserData());
        if (message.startsWith("Error:")) {
            return ResponseEntity.internalServerError().body(requestHelper.authErrorResponse(message, null));
        }

        return requestHelper.responseWithJWT(requestHelper.authSuccessResponse(message, res.getUserData().getId()),res.getUserData().getRole(),true);
    }


    public ResponseEntity<AuthResponse> resetPassword(String OTP, String newPassword, String cookieToken) {
        JwtResponse tokenResponse = requestHelper.validateJWT(cookieToken);
        if (tokenResponse == null || tokenResponse.isError()) {
            String message = tokenResponse != null && tokenResponse.getMessage() != null
                    ? tokenResponse.getMessage()
                    : "Invalid token";
            return ResponseEntity.status(401).body(requestHelper.authErrorResponse(message,null));
        }
        Long id = tokenResponse.getId();
        List<CheckMessage> errorData = new ArrayList<>();
        boolean isError = false;
        if(requestHelper.isBlank(OTP)){
            isError = true;
            errorData.add(new CheckMessage(true, "OTP", "OTP cannot be empty"));
        }
        if (requestHelper.isBlank(newPassword)) {
            isError = true;
            errorData.add(new CheckMessage(true, "NewPassword", "New Password cannot be empty"));
        }
        if (isError) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("not a valid request", errorData));
        }
        FunctionMessage otpStatus = otpHelper.verifyOtp(OTP, id);
        if (otpStatus == null || otpStatus.isError()) {
            String message = otpStatus != null && otpStatus.getMessage() != null
                    ? otpStatus.getMessage()
                    : "OTP verification failed";
            return ResponseEntity.status(401).body(requestHelper.authErrorResponse(message,null));
        }
        UserCred userCred = new UserCred();
        userCred.setPassword(newPassword);
        userCred.setId(id);
        UserDBResponse response = userCredDBHandler.updateUserCred(userCred);
        if (response.isError()) {
            return ResponseEntity.internalServerError().body(requestHelper.authErrorResponse(response.getMessage(), null));
        }
        return ResponseEntity.ok().body(requestHelper.authSuccessResponse(response.getMessage(), id));
    }

    public ResponseEntity<AuthResponse> sendVerificationEmail(String email) {
        if (requestHelper.isBlank(email)) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("Email cannot be empty", null));
        }
        UserDBResponse data = userCredDBHandler.findUserCredByGmail(email);
        if (data == null || data.isError() || null==data.getUserData()) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("No user found with email " + email,null));
        }
        UserCred user = data.getUserData();
        if (user.isVerified()) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("Account already verified",null));
        }
        helper.sendVerificationEmail(email, requestHelper.generateOTPToken(user.getId(), true));
        return ResponseEntity.ok().body(requestHelper.authSuccessResponse("Email is sent for verification",user.getId()));
    }

    public ResponseEntity<String> verifyEmail(String cookieToken) {
        JwtResponse tokenResponse = requestHelper.validateJWT(cookieToken);
        if (tokenResponse == null ||  (boolean) tokenResponse.isError()) {
            String message = tokenResponse != null && tokenResponse.getMessage() != null
                    ? tokenResponse.getMessage()
                    : "Invalid token";
            return requestHelper.responseWithHTMLMessage(message);
        }
        Long id = requestHelper.asLong(tokenResponse.getId());
        UserDBResponse data = userCredDBHandler.findUserCredById(id);
        if (data == null || data.isError() || null==data.getUserData()) {
            return requestHelper.responseWithHTMLMessage("No user found with id " + id);
        }
        UserCred user = data.getUserData();
        if (user.isVerified()) {
            return requestHelper.responseWithHTMLMessage("Account already verified");
        }
        user.setVerified(true);
        userCredDBHandler.saveUserCred(user);
        return requestHelper.responseWithHTMLMessage("Email verified successfully");
    }

    public ResponseEntity<AuthResponse> refreshToken(String token) {
        JwtResponse tokenResponse = requestHelper.validateJWT(token);
        if (tokenResponse == null || tokenResponse.isError()) {
            String message = tokenResponse != null && tokenResponse.getMessage() != null
                    ? tokenResponse.getMessage()
                    : "Invalid token";
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse(message,null));
        }
        Long id = requestHelper.asLong(tokenResponse.getId());
        UserDBResponse data = userCredDBHandler.findUserCredById(id);
        if (data == null || data.isError() || null==data.getUserData()) {
            return ResponseEntity.badRequest().body(requestHelper.authErrorResponse("No user found with id " + id,null));
        }
        UserCred userCred = data.getUserData();
        LocalDateTime now = LocalDateTime.now();
        Date expiresAt =  tokenResponse.getExpiryTime();
        LocalDateTime expiresAtLocal = expiresAt.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (expiresAtLocal.isAfter(now.plusMinutes(2))) {
            return ResponseEntity.badRequest().body(new AuthResponse(true,null, "Token not close to expiry",id));
        }
        return requestHelper.responseWithJWT(requestHelper.authSuccessResponse("Login successful", userCred.getId()),userCred.getRole(),false);
    }


    private UserDBResponse lookupUserByUsernameOrEmail(String userName, String email) {
        if (!requestHelper.isBlank(userName)) {
            return userCredDBHandler.findUserCredByUsername(userName);
        }
        return userCredDBHandler.findUserCredByGmail(email);
    }




}
