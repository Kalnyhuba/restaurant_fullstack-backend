package com.restaurant.restaurantbackend.security.role_based_auth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class User {

    /*@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;*/

    @Id
    private String username;

    private String firstName;

    private String lastName;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLE",
            joinColumns = {
                @JoinColumn(name = "USER_ID")
            },
            inverseJoinColumns = {
                @JoinColumn(name = "ROLE_ID")
            }
    )
    private Set<Role> roles;
}
