package com.proyecto.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSecResponseDTO {

    private String username;
    private Set<String> rolesList;
    private Long authorId; // Si el usuario es autor, devuelve solo su ID
}
