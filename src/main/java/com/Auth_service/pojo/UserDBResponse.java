package com.Auth_service.pojo;


import java.util.List;

import com.Auth_service.Entity.UserCred;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDBResponse {
    String message;
    boolean isError;    
    UserCred userData;
}

