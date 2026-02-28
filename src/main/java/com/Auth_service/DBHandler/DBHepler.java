package com.Auth_service.DBHandler;

import com.Auth_service.Entity.UserCred;
import com.Auth_service.pojo.UserDBResponse;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBHepler {
    public UserDBResponse userErrorResponse(String message) {
        UserDBResponse userResponse = new UserDBResponse();
        userResponse.setMessage(message);
        userResponse.setUserData(null);
        userResponse.setError(true);
        return userResponse;
    }
    public UserDBResponse userSuccessResponseList(String message, UserCred userData) {
        UserDBResponse userResponse = new UserDBResponse();
        userResponse.setMessage(message);
        userResponse.setUserData(userData);
        userResponse.setError(false);
        return userResponse;
    }
}
