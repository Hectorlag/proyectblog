package com.proyecto.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UserDTO {


    private String username;
    private String password;
    private boolean isAuthor;  // Indicador de si el usuario es un AUTHOR
}
