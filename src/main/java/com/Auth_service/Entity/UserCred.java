package com.Auth_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usercred") // Use 'name' attribute for JPA
public class UserCred {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name ="role")
    private String role;

    public UserCred(String userName, String email, String password,String role) {
        this.setUserName(userName);
        this.setEmail(email);
        this.setPassword(password);
        this.setRole(role);
    }
}
