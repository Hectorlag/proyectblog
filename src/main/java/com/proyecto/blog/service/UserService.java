package com.proyecto.blog.service;

import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserSecService{

    @Autowired
    private IUserSecRepository userSecRepository; // Inyección del repositorio de UserSec

    @Override
    public UserSec createUserSec(UserSec userSec) {

        return userSecRepository.save(userSec); // Guardar el nuevo UserSec
    }

    @Override
    public Optional<UserSec> getUserSecById(Long id) {
        // Buscar un UserSec por su id. Devuelve un Optional
        return userSecRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<UserSec> getAllUserSecs() {
        // Obtener todos los UserSecs desde la base de datos
        return userSecRepository.findByDeletedFalse();
    }

    @Override
    public UserSec updateUserSec(Long id, UserSec userSecDetails) {
        // Buscar el UserSec por id
        UserSec existingUserSec = userSecRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("UserSec not found with id: " + id));

        // Actualizar los detalles
        existingUserSec.setUsername(userSecDetails.getUsername());
        existingUserSec.setPassword(userSecDetails.getPassword());
        existingUserSec.setEnabled(userSecDetails.isEnabled());
        existingUserSec.setAccountNotLocked(userSecDetails.isAccountNotLocked());
        existingUserSec.setAccountNotExpired(userSecDetails.isAccountNotExpired());
        existingUserSec.setCredentialNotExpired(userSecDetails.isCredentialNotExpired());

        // Guardar los cambios
        return userSecRepository.save(existingUserSec);
    }

    @Override
    public boolean deleteUserSec(Long id) {
        // Verificar si el UserSec existe antes de eliminarlo
        UserSec userSec = userSecRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("UserSec not found with id: " + id));

        // Eliminar el UserSec
        userSec.setDeleted(true); // Marcamos el UserSec como eliminado
        userSecRepository.delete(userSec); // Guardamos en la base de datos

        return true; //
    }

    //agregamos el método encript password en UserService
    @Override
    public String encriptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
