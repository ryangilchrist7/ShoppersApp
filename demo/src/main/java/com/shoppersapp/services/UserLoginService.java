package com.shoppersapp.services;

import com.shoppersapp.repositories.UserRepository;
import com.shoppersapp.model.User;
import com.shoppersapp.dto.UserLoginDTO;
import com.shoppersapp.utils.PasswordUtils;

import java.util.Optional;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {
    private final UserRepository userRepository;
    /*
     * Service to manage user login
     */

    @Autowired
    public UserLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserLoginDTO login(String identifier, String password) {
        try {
            Optional<User> userOpt = userRepository.findByEmailOrPhoneNumber(identifier, identifier);
            if (userOpt.isEmpty()) {
                throw new AuthenticationException("That user does not exist");
            }

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                byte[] storedHash = user.getPasswordHash();
                if (!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                    throw new AuthenticationException("Those login details are invalid");
                }
                if (PasswordUtils.verifyPassword(password, storedHash)) {
                    return new UserLoginDTO(user.getUserId(), "Login successful");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
