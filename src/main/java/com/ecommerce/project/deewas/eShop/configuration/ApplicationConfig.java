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

    //defining our own custom authentication as a function. Or you can also implement a AuthenticationProvider interface to create a custom AuthenticationProvider
    //The purpose of this code is authentication, not user creation. It uses UserDetailsService to load user details (e.g., from the database) and a PasswordEncoder to verify the password during login attempts.
    //Returns a valid Authentication object if the credentials are correct.
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); //customUserDetailsService is injected to let the DaoAuthenticationProvider fetch users during authentication.
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12)); //the PasswordEncoder is used to verify the password.
        return authProvider; // This registers the DaoAuthenticationProvider bean, making it available for Spring Security to use during authentication.
    }


    //    The AuthenticationManager is a central interface in Spring Security. It handles the entire authentication process by:
//    Delegating authentication requests to one or more authentication providers (like DaoAuthenticationProvider).
//    Returning a valid Authentication object if the credentials are valid, or throwing an exception if not.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class); //This retrieves the AuthenticationManagerBuilder, which allows you to register custom authentication providers like the DaoAuthenticationProvider.
        auth.authenticationProvider(daoAuthenticationProvider()); // Manually registering our custom defined daoAuthenticationProvider to authenticate users
//        auth.anotherAuthenticatonProvider();
        return auth.build(); // This constructs the AuthenticationManager with the registered DaoAuthenticationProvider
    }   // As a whole, this return statement registers the AuthenticationManager bean, making it available for injection in other parts of your application, such as a service class


    //it is generally necessary to explicitly define an AuthenticationManager bean here in SecurityConfig file, if you need to access it outside the security filter chain, such as in a service or controller file.
//    this approach retrieves the default AuthenticationManager managed by Spring Security’s auto-configuration.
//    The AuthenticationConfiguration class knows how to build an AuthenticationManager using any UserDetailsService or AuthenticationProvider beans present in the context.
//    If you have already declared a UserDetailsService bean, Spring will automatically register it with a default DaoAuthenticationProvider.

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager(); //If authentication is successful, it returns a fully populated Authentication object
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
