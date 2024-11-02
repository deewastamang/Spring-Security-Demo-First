package com.ecommerce.project.deewas.eShop.configuration;

import com.ecommerce.project.deewas.eShop.entity.CustomUserDetails;
import com.ecommerce.project.deewas.eShop.repository.TokenRepository;
import com.ecommerce.project.deewas.eShop.service.CustomUserDetailsService;
import com.ecommerce.project.deewas.eShop.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); //passing the request to another filter in the chain
            return;
        }

        jwtToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwtToken);

        if (userEmail == null && SecurityContextHolder.getContext().getAuthentication() != null) { // checking if we got userEmail and user is authenticated
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // Double-check the validity of token of the database side and jwtService. We check if the token is valid with jwt and also rely on database that our token is not expired or revoked
        var isTokenValid = tokenRepository.findByToken(jwtToken)  //This will forbid old tokens of the user from accessing protected resources
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);


        if (jwtService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("auth token: " + authToken.getPrincipal());
            // OUTPUT: auth token: UsernamePasswordAuthenticationToken [Principal=com.ecommerce.project.deewas.eShop.entity.CustomUserDetails@33a75e7d, Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=null], Granted Authorities=[MANAGER_UPDATE, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE, MANAGER_DELETE, MANAGER_CREATE, MANAGER_READ, ADMIN_READ, ROLE_ADMIN]]
        }
        filterChain.doFilter(request, response);  // passing request and response to the other filters in the security chain
    }
}
