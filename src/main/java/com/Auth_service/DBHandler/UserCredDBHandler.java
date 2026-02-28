package com.Auth_service.DBHandler;
import com.Auth_service.Entity.UserCred;
import com.Auth_service.Repository.UserCredRepository;
import com.Auth_service.pojo.AuthResponse;
import com.Auth_service.pojo.CheckMessage;
import com.Auth_service.pojo.RegisterRequest;
import com.Auth_service.pojo.UserDBResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserCredDBHandler {

    @Autowired
    UserCredRepository userCredRepository;
    @Autowired
    DBHepler Helper;


    // -------------------- UserCred --------------------

//    public UserDBResponse findAllUserCreds() {
//        try {
//            List<UserCred> list = (List<UserCred>) userCredRepository.findAll();
//            return Helper.userSuccessResponseList("All user creds retrieved", list);
//        } catch (Exception e) {
//            return Helper.userErrorResponse("Error occurred: " + e.getMessage());
//        }
//    }

    public UserDBResponse findUserCredById(Long id) {
        try {
            Optional<UserCred> optionalUser = userCredRepository.findById(id);
            if (optionalUser.isPresent()) {
                return Helper.userSuccessResponseList("User with id " + id + " found", optionalUser.get());
            } else {
                return Helper.userErrorResponse("No user found with id " + id);
            }
        } catch (Exception e) {
            return Helper.userErrorResponse("Error occurred: " + e.getMessage());
        }
    }

    public void saveUserCred(UserCred userCred) {
        try {
            UserCred saved = userCredRepository.save(userCred);
            Helper.userSuccessResponseList("User " + saved.getUserName() + " saved", saved);
        } catch (Exception e) {
            Helper.userErrorResponse("Error occurred while saving: " + e.getMessage());
        }
    }

    public UserDBResponse deleteUserCredById(Long id) {
        try {
            if (userCredRepository.existsById(id)) {
                userCredRepository.deleteById(id);
                return Helper.userSuccessResponseList("User with id " + id + " deleted", null);
            } else {
                return Helper.userErrorResponse("No user found with id " + id);
            }
        } catch (Exception e) {
            return Helper.userErrorResponse("Error occurred while deleting: " + e.getMessage());
        }
    }

    public UserDBResponse findUserCredByGmail(String email) {
        try {
            Optional<UserCred> optionalUser = userCredRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                return Helper.userSuccessResponseList("User " + email + " found", optionalUser.get());
            } else {
                return Helper.userErrorResponse("No user found with email " + email);
            }
        } catch (Exception e) {
            return Helper.userErrorResponse("Error occurred: " + e.getMessage());
        }
    }

    public UserDBResponse findUserCredByUsername(String username) {
        try {
            Optional<UserCred> optionalUser = userCredRepository.findByUserName(username);
            if (optionalUser.isPresent()) {
                return Helper.userSuccessResponseList("User " + username + " found", optionalUser.get());
            } else {
                return Helper.userErrorResponse("No user found with username " + username);
            }
        } catch (Exception e) {
            return Helper.userErrorResponse("Error occurred: " + e.getMessage());
        }
    }

    public AuthResponse registerDBCheckAndSave(RegisterRequest request) {
        boolean isError = false;
        List<CheckMessage> messages = new ArrayList<>();

        try {
            var optionalEmailUser = userCredRepository.findByEmail(request.getEmail());
            if (optionalEmailUser.isPresent()) {
                isError = true;
                messages.add(new CheckMessage( true,"email", "An account exists with this Email"));
            }
        } catch (Exception e) {
            isError = true;
            messages.add(new CheckMessage( true,"database", "An error occurred while checking Email"));
        }

        try {
            var optionalUsernameUser = userCredRepository.findByUserName(request.getUserName());
            if (optionalUsernameUser.isPresent()) {
                isError = true;
                messages.add(new CheckMessage( true,"username", "An account exists with this Username"));
            }
        } catch (Exception e) {
            isError = true;
            messages.add(new CheckMessage( true,"database", "An error occurred while checking Username"));
        }
        if (!isError) {
            UserCred requestUser = new UserCred(request.getUserName(), request.getEmail(), request.getPassword(),"USER");
            try {
                UserCred newUser = userCredRepository.save(requestUser);
                return new AuthResponse(false, messages, "User registered successfully", newUser.getId());
            } catch (Exception e) {
                isError = true;
                messages.add(new CheckMessage(isError,"database", "An error occurred while saving user data"));
            }
        }

        return new AuthResponse(isError, messages, "Registration checks failed", null);

    }
public UserDBResponse updateUserCred(UserCred updatedUserCred) {
        if (updatedUserCred == null || updatedUserCred.getId() == null) {
            return Helper.userErrorResponse("UserCred or ID must not be null");
        }
        try {
            Optional<UserCred> existingUserOpt = userCredRepository.findById(updatedUserCred.getId());
            if (existingUserOpt.isPresent()) {
                UserCred existingUser = updateExistingUser(updatedUserCred, existingUserOpt.get());
                UserCred savedUser = userCredRepository.save(existingUser);
                return Helper.userSuccessResponseList("User updated successfully", savedUser);
            } else {
                return Helper.userErrorResponse("No user found with id " + updatedUserCred.getId());
            }
        } catch (Exception e) {
            return Helper.userErrorResponse("Error occurred while updating: " + e.getMessage());
        }
    }

    private static UserCred updateExistingUser(UserCred updatedUserCred, UserCred existingUser) {
        if (updatedUserCred.getUserName() != null) {
            existingUser.setUserName(updatedUserCred.getUserName());
        }
        if (updatedUserCred.getEmail() != null) {
            existingUser.setEmail(updatedUserCred.getEmail());
        }
        if (updatedUserCred.getPassword() != null) {
            existingUser.setPassword(updatedUserCred.getPassword());
        }
        return existingUser;
    }

}

