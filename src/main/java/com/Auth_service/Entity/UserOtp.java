package com.Auth_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_otp")
public class UserOtp {
    @Id
    private Long id;  // Also foreign key to usercred.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserCred userCred;

    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;

    @Column(name = "count")
    private Integer count;

    @Column(name = "reset_time", nullable = false)
    private LocalDateTime resetTime;

    @Column(name ="Password_reset_time")
    private LocalDateTime passwordResetTime;

}
