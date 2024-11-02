package com.ecommerce.project.deewas.eShop.service;

import com.ecommerce.project.deewas.eShop.entity.CustomUserDetails;
import com.ecommerce.project.deewas.eShop.entity.User;
import com.ecommerce.project.deewas.eShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//Since UserDetailsService is an interface, we have to implement the class and define our custom UserDetailsService to load user-specific data from the database
//it helps to retrieve user data from the database during authentication. It is part of Spring Security and defines a single method to load user details by username (or email).
@Service
public class CustomUserDetailsService implements UserDetailsService  {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if(user == null) {
            System.out.println("User not found with email " + email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }


        return new CustomUserDetails(user);  //returns an instance of UserDetails with user's email, password and role
    }
}
