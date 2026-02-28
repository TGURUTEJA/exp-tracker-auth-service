package com.Auth_service.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    boolean isError;
    List<CheckMessage> errorData;
    String message;
    Long ID;
}

