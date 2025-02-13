package com.proyecto.blog.service;

import com.proyecto.blog.dto.UserDTO;
import com.proyecto.blog.excepcion.RoleNotFoundException;
import com.proyecto.blog.excepcion.UserNotFoundException;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IAuthorRepository;
import com.proyecto.blog.repository.IRoleRepository;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements IUserSecService {

    @Autowired
    private IUserSecRepository userSecRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IAuthorRepository iAuthorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyección del BCryptPasswordEncoder

    public UserSec registerUser(UserDTO userDTO, boolean isAuthor) {
        // Crear el usuario
        UserSec user = new UserSec();
        user.setUsername(userDTO.getUsername());
        user.setPassword(this.encriptPassword(userDTO.getPassword()));  // Encriptar la contraseña
        user.setEnabled(true);
        user.setAccountNotLocked(true);
        user.setAccountNotExpired(true);
        user.setCredentialNotExpired(true);

        // Asignar rol
        Role role = roleRepository.findByRole(isAuthor ? "AUTHOR" : "USER")
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        user.setRolesList(Collections.singleton(role));

        // Guardar usuario
        UserSec savedUser = userSecRepository.save(user);

        // Si es autor, creamos automáticamente el Author
        if (isAuthor) {
            Author author = new Author();
            author.setUser(savedUser);
            iAuthorRepository.save(author);
            savedUser.setAuthor(author);  // Establecer la relación en el UserSec
        }

        return savedUser;
    }

    @Override
    public Optional<UserSec> getUserSecById(Long id) {
        return userSecRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<UserSec> getAllUserSecs() {
        return userSecRepository.findByDeletedFalse();
    }

    public UserSec updateUser(Long id, UserDTO userDTO, boolean isAuthor) {
        UserSec existingUser = userSecRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Actualizar solo los campos que se envían
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(this.encriptPassword(userDTO.getPassword()));
        }

        // Actualizar rol según `isAuthor`
        Role newRole = roleRepository.findByRole(isAuthor ? "AUTHOR" : "USER")
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        existingUser.setRolesList(Collections.singleton(newRole));

        // Si el usuario es autor, gestionar la relación con `Author`
        if (isAuthor) {
            if (existingUser.getAuthor() == null) {
                Author author = new Author();
                author.setUser(existingUser);
                iAuthorRepository.save(author);
                existingUser.setAuthor(author);
            }
        } else {
            existingUser.setAuthor(null); // Si ya no es autor, eliminamos la relación
        }

        return userSecRepository.save(existingUser);
    }


    @Override
    public boolean deleteUserSec(Long id) {
        UserSec userSec = userSecRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("UserSec not found with id: " + id));

        userSec.setDeleted(true);
        userSecRepository.save(userSec);

        return true;
    }

    @Override
    public String encriptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}

