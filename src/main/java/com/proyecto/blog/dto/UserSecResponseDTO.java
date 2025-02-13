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
    private Set<String> rolesList; // Solo los roles en forma de lista de strings (o lo que necesites)
}
