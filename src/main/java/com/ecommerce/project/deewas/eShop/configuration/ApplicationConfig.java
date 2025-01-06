package com.ecommerce.project.deewas.eShop.configuration;

import com.ecommerce.project.deewas.eShop.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public ApplicationConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }


// This method defines a custom AuthenticationProvider bean (DaoAuthenticationProvider).
// The only purpose of this code is authentication, not user creation. It uses UserDetailsService to load user details (e.g., from the database) and a PasswordEncoder to verify the password during login attempts.
// If authentication succeeds, it returns a valid Authentication object; otherwise, it throws an exception.
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();

        // Setting the custom UserDetailsService to fetch user details during authentication.
        daoAuthProvider.setUserDetailsService(customUserDetailsService); 

        // Setting a PasswordEncoder to securely hash and validate user passwords.
        daoAuthProvider.setPasswordEncoder(passwordEncoder()); 

        // Registering and returning the DaoAuthenticationProvider bean to be used by Spring Security.
        return authProvider; 
    }



    //    The AuthenticationManager is like the orchestrator of the authentication process. It delegates the authentication task to one or more AuthenticationProviders.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class); //This retrieves the AuthenticationManagerBuilder, which allows you to register custom authentication providers like the DaoAuthenticationProvider.
        auth.authenticationProvider(daoAuthenticationProvider()); // Manually registering our custom defined daoAuthenticationProvider to authenticate users
//        auth.anotherAuthenticatonProvider();
        return auth.build(); // This constructs the AuthenticationManager with the registered DaoAuthenticationProvider
    }   // As a whole, this return statement registers the AuthenticationManager bean, making it available for injection in other parts of your application, such as a service class

    // it is generally necessary to explicitly define an AuthenticationManager bean here in Config file, if you need to access it outside the security filter chain, such as in a service or controller file.
//    this approach retrieves the default AuthenticationManager managed by Spring Security’s auto-configuration.
//    The AuthenticationConfiguration class knows how to build an AuthenticationManager using any UserDetailsService or AuthenticationProvider beans present in the context.
//    If you have already declared a UserDetailsService bean, Spring will automatically register it with a default DaoAuthenticationProvider.

// HOW THEY WORK TOGETHER:
// A user tries to log in by providing credentials (e.g., username and password).
// The AuthenticationManager receives the credentials and forwards them to the appropriate AuthenticationProvider.
// If any one of the AuthenticationProvider validates the credentials, it returns an authenticated user object (Authentication).
// If none of the AuthenticationProvider can validate, then it throws an exception.
// The AuthenticationManager checks the result and allows or denies access based on the outcome.




// The PasswordEncoder interface is used to encode and verify passwords. It is used to securely store passwords in the database and also to verify user input during login attempts.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); //This registers the PasswordEncoder bean, making it available for Spring Security to use during authentication.
    }


}
