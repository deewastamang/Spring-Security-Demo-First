package com.ecommerce.project.deewas.eShop.configuration;

import com.ecommerce.project.deewas.eShop.service.CustomUserDetailsService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.ecommerce.project.deewas.eShop.entity.enums.Permission.*;
import static com.ecommerce.project.deewas.eShop.entity.enums.Role.*;

@Configuration
@EnableWebSecurity  //Disable default spring security configuration and enable this custom configuration
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/login",
            "/api/register",
            "/api/refresh-token"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider daoAuthenticationProvider;
    private final LogoutHandler logoutHandler;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider daoAuthenticationProvider, LogoutHandler logoutHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//          Lone Way to define http
//        http.csrf(customizer -> customizer.disable()); //  disabling csrf
//        http.authorizeHttpRequests(request -> request.anyRequest().authenticated());  // No one can access the api without the authentication
//        http.formLogin(Customizer.withDefaults()); //Enables users to get authenticated with a login form in the browser
//        http.httpBasic(Customizer.withDefaults()); //Enable users to send username and password to get authenticated through postman
//        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //will get new session id everytime you make request while being authenticated

        http
                .csrf(customizer -> customizer.disable()) //  disabling csrf

                // Authorize HTTP requests based on roles and authorities
                .authorizeHttpRequests(req -> req

                        // Public routes
                        .requestMatchers(WHITE_LIST_URL)
                        .permitAll()

                        // Admin routes - Secure specific HTTP operations
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasAuthority(ADMIN_READ.name()) //securing different http operations to be only performed by admin
                        .requestMatchers(HttpMethod.POST, "/api/admin/**").hasAuthority(ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/admin/**").hasAuthority(ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasAuthority(ADMIN_DELETE.name())
                        .requestMatchers("/api/admin/**").hasRole(ADMIN.name())  // Protecting whole /api/admin route to be accessible by only admin

                        // User routes - Secure specific HTTP operations
                        .requestMatchers(HttpMethod.GET, "/api/user/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name()) //securing different http operations to be only performed by authorized mentioned role
                        .requestMatchers(HttpMethod.POST, "/api/user/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/user/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/user/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                        .requestMatchers("/api/user/**").hasAnyRole(ADMIN.name(), MANAGER.name())  // Protecting whole /api/user route to be accessible by only admin and manager

                        // Free routes optional. you can add in White list urls to make it more public systematic way
                        .requestMatchers("/api/free").permitAll()  // Allow access to /api/free without authentication

                        // Require authentication for all other requests
                        .anyRequest().authenticated() // All other routes require authentication

                )

                // Enable login mechanisms
                .formLogin(Customizer.withDefaults()) // Enable form login for browser-based authentication
                .httpBasic(Customizer.withDefaults()) // Enable HTTP Basic for API clients like Postman

                // Session management (stateless for JWT-based authentication)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management

                // Register authentication provider and JWT filter
                .authenticationProvider(daoAuthenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // run jwtAuthFilter that we created before UsernamePasswordAuthenticationFilter

                // Logout Mechanism
                .logout(logout -> logout
                    .logoutUrl("/api/logout") // Logout endpoint. by default it's "/logout"
                    .addLogoutHandler(logoutHandler) // // My custom LogoutHandler
                    .logoutSuccessHandler((request, response, authentication) ->
                            SecurityContextHolder.clearContext() // // Clear security context that holds authentication principal
                    )

                )
        ;


        return http.build();   // build and returns the object of SecurityFilterChain
    }


}
