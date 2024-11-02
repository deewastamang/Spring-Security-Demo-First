package com.ecommerce.project.deewas.eShop.service;

import com.ecommerce.project.deewas.eShop.dto.AuthenticationResponse;
import com.ecommerce.project.deewas.eShop.dto.LoginRequestDto;
import com.ecommerce.project.deewas.eShop.dto.UserRegisterDto;
import com.ecommerce.project.deewas.eShop.entity.CustomUserDetails;
import com.ecommerce.project.deewas.eShop.entity.Token;
import com.ecommerce.project.deewas.eShop.entity.User;
import com.ecommerce.project.deewas.eShop.entity.enums.Role;
import com.ecommerce.project.deewas.eShop.entity.enums.TokenType;
import com.ecommerce.project.deewas.eShop.repository.TokenRepository;
import com.ecommerce.project.deewas.eShop.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, TokenRepository tokenRepository, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Instantiate encoder
    }


    public AuthenticationResponse registerUser(UserRegisterDto userRegisterDto) {

//        if(!userRegisterDto.isPasswordMatching()) {
//            throw new IllegalArgumentException("Password do not match");
//        }

        String encryptedPassword = passwordEncoder.encode(userRegisterDto.getPassword());

        var user = User.builder()  // with .builder(), you can create a complex object step-by-step without having to provide all the parameters at once.
                .firstName(userRegisterDto.getFirstName())
                .lastName(userRegisterDto.getLastName())
                .email(userRegisterDto.getEmail())
                .password(encryptedPassword)
                .age(userRegisterDto.getAge())
                .gender(userRegisterDto.getGender())
                .address(userRegisterDto.getAddress())
                .role(Role.USER)
                .build();  // Constructs and returns the final User object using the parameters set in the Builder.

        var savedUser = userRepository.save(user);

        //generate jwt token and refresh token and save user token
        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        var refreshToken = jwtService.generateRefreshToken(new CustomUserDetails(user));

        // save generated token to the database
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();


    }


    public AuthenticationResponse loginUser(LoginRequestDto loginRequestDto) {

        // Create an authentication token with the user's input email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        // If authentication is not successful, return a failure response
        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        // If authentication is successful, proceed with JWT generation (or any response)
        var user = (CustomUserDetails) authentication.getPrincipal(); // Cast to your custom UserDetails if needed
//            System.out.println("Authenticated user: " + user); // OUTPUT: Authenticated user: com.ecommerce.project.deewas.eShop.entity.CustomUserDetails@65962948
//            System.out.println("Authenticated user: " + user.getEmail()); // OUTPUT: Authenticated user: deewastamang@gmail.com

        //generate jwt token and return refresh token and access token
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // we need user of type "User" but our "user" is of type UserDetails. so, we need to extract user from database
        var userFromDatabase = userRepository.findByEmail(user.getEmail());

        // first revoking the token of user if it already exists, so that only 1 token is set to one user
        revokeAllUserTokens(userFromDatabase);

        // save the generated token to the database
        saveUserToken(userFromDatabase, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Authorization header is missing or invalid");
            return;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid refresh token");
            return;
        }

        var user = this.userRepository.findByEmail(userEmail);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("User not found");
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail()); // doing this because we need to pass userDetails in the parameter of isTokenValid() function of type UserDetails

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // Generate new access token and save it
        var accessToken = jwtService.generateToken(userDetails);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

    }
}
