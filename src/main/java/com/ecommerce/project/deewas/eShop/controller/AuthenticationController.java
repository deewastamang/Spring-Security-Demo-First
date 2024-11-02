package com.ecommerce.project.deewas.eShop.controller;

import com.ecommerce.project.deewas.eShop.dto.AuthenticationResponse;
import com.ecommerce.project.deewas.eShop.dto.LoginRequestDto;
import com.ecommerce.project.deewas.eShop.dto.UserRegisterDto;
import com.ecommerce.project.deewas.eShop.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path="api")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //Create (Register) a new user
    @PostMapping(value="register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        try {
            // Call the authentication service to register the user
            AuthenticationResponse response = authenticationService.registerUser(userRegisterDto);
            return ResponseEntity.ok(response); // 200 OK on success
        } catch (IllegalArgumentException e) {
            // Handle validation errors or bad input
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder()
                            .error("Invalid input: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            // Handle any unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthenticationResponse.builder()
                            .error("An unexpected error occurred: " + e.getMessage())
                            .build());
        }
    }

    //Authenticate user login
    @PostMapping(value="login")
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            // Call the authentication service to register the user
            AuthenticationResponse response = authenticationService.loginUser(loginRequestDto);
            return ResponseEntity.ok(response); // 200 OK on success

        } catch (BadCredentialsException e) {
            // Handle validation errors or bad input
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder()
                            .error("Invalid Email or Password: " + e.getMessage())
                            .build());

        } catch (AuthenticationException e) {
            // Handle validation errors or bad input
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder()
                            .error("Unable to authenticate: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            // Handle any unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthenticationResponse.builder()
                            .error("An unexpected error occurred: " + e.getMessage())
                            .build());
        }
    }

    //Refresh Token to generate new access token
    @PostMapping(value="refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
