package com.proyecto.blog.service;

import com.proyecto.blog.model.UserSec;

import java.util.List;
import java.util.Optional;

public interface IUserSecService {

    UserSec createUserSec(UserSec userSec); // Crear un nuevo UserSec
    Optional<UserSec> getUserSecById(Long id); // Obtener UserSec por id
    List<UserSec> getAllUserSecs(); // Obtener todos los UserSecs
    UserSec updateUserSec(Long id, UserSec userSecDetails); // Actualizar un UserSec
    void deleteUserSec(Long id); // Eliminar un UserSec
}
