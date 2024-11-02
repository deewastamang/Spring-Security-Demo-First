package com.ecommerce.project.deewas.eShop.entity;

import com.ecommerce.project.deewas.eShop.entity.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true) // Tokens should be unique and not null
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false) // Adjusted column name for consistency
    private TokenType tokenType;

    @Column(nullable = false)
    private boolean expired;

    @Column(nullable = false)
    private boolean revoked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false) // Adjusted column name for consistency
    private User user;
}
