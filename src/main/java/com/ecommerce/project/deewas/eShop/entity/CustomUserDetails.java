package com.ecommerce.project.deewas.eShop.entity;


import com.ecommerce.project.deewas.eShop.entity.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


// we also have to define our own custom UserDetails class by implementing UserDetails
// UserDetails is a key interface in Spring Security that represents the user information required for authentication and authorization. 
// It acts as a contract that Spring Security uses to load the user details from a source (such as a database, LDAP, or in-memory storage) and make the user’s information available for security-related operations, such as login and access control.

public class CustomUserDetails implements UserDetails {

    private final User user;  //this user is loaded from database

    public CustomUserDetails(User user) {   // will get the argument from CustomUserDetailsService's loadUserByUsername() method
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().getAuthorities(); // Get the authorities from the User's role
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


// Another good method to include all User fields in CustomUserDetails:
// public class CustomUserDetails implements UserDetails {

//     private Long id;  // The ID from the User entity
//     private String username;
//     private String password;
//     private List<GrantedAuthority> authorities;

//     private String email;
//     private int age;
//     private String gender;
//     private String address;

//     private LocalDateTime createdAt;  // The creation timestamp
//     private LocalDateTime updatedAt;  // The last updated timestamp

//     public CustomUserDetails(User user) {
//         this.id = user.getId();
//         this.username = user.getUsername();
//         this.password = user.getPassword();
//         this.authorities = AuthorityUtils.createAuthorityList(user.getAuthorities().toArray(new String[0]));
//         this.email = user.getEmail();
//         this.age = user.getAge();
//         this.gender = user.getGender();
//         this.address = user.getAddress();
//         this.createdAt = user.getCreatedAt();
//         this.updatedAt = user.getUpdatedAt();
//     }

//     @Override
//     public Collection<? extends GrantedAuthority> getAuthorities() {
//         return authorities;
//     }

//     @Override
//     public String getPassword() {
//         return password;
//     }

//     @Override
//     public String getUsername() {
//         return username;
//     }

//     public Long getId() {
//         return id;
//     }

//     public LocalDateTime getCreatedAt() {
//         return createdAt;
//     }

//     public LocalDateTime getUpdatedAt() {
//         return updatedAt;
//     }

//     // Implement other methods from UserDetails...

//     public String getEmail() {
//         return email;
//     }

//     public int getAge() {
//         return age;
//     }

//     public String getGender() {
//         return gender;
//     }

//     public String getAddress() {
//         return address;
//     }
// }
