package com.Auth_service.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private boolean isError;
    private String message;
    private Long Id;
    private Date expiryTime;
    private boolean passwordChanged;
    private String role;
}
