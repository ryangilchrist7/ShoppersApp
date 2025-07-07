package com.shoppersapp.controllers;

import com.shoppersapp.services.UserRegistrationService;
import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.payload.RegisterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRegistrationService registrationService;

    @Autowired
    public UserController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest req) {
        try {
            registrationService.registerUser(
                    req.getFirstName(),
                    req.getDateOfBirth(),
                    req.getLastName(),
                    req.getPhoneNumber(),
                    req.getAddress(),
                    req.getEmail(),
                    req.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (IdentifierInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }
}