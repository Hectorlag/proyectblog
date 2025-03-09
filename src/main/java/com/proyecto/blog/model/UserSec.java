package com.proyecto.blog.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = true)
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNotLocked;
    private boolean accountNotExpired;
    private boolean credentialNotExpired;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)//eager carga todos los roles
    @JoinTable(name="user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns=@JoinColumn(name = "role_id"))
    private Set<Role> rolesList = new HashSet<>();

    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Author author;
}
