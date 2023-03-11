package com.restaurant.restaurantbackend.security.role_based_auth.token.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @EmbeddedId
    private TokenKey tokenId;

    @Column(nullable = false)
    private Long expiresAt;

    @Column(nullable = false)
    private String purpose;
}
