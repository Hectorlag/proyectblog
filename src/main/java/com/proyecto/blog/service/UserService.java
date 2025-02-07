package com.proyecto.blog.service;

import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements IUserSecService {

    @Autowired
    private IUserSecRepository userSecRepository; // Inyección del repositorio de UserSec

    @Autowired
    private RoleService roleService;

    public UserSec createUserSec(UserSec userSec) {
        try {
            // Encriptamos la contraseña antes de guardar
            userSec.setPassword(encriptPassword(userSec.getPassword()));

            // Inicializamos la lista si es null
            if (userSec.getRolesList() == null) {
                userSec.setRolesList(new HashSet<>());
            }

            // Si el usuario no tiene roles asignados, se le asigna el rol USER por defecto
            if (userSec.getRolesList().isEmpty()) {
                Role userRole = roleService.getRoleByName("USER")
                        .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado en la BD"));

                // Asegurar que el Role es gestionado por Hibernate antes de asignarlo
                userRole = roleService.getRoleById(userRole.getId()).orElse(userRole);

                userSec.getRolesList().add(userRole);
            } else {
                // Verificamos y convertimos los roles antes de guardarlos
                Set<Role> managedRoles = new HashSet<>();
                for (Role role : userSec.getRolesList()) {
                    Role managedRole = roleService.getRoleById(role.getId())
                            .orElseThrow(() -> new RuntimeException("Error: Rol con ID " + role.getId() + " no encontrado"));
                    managedRoles.add(managedRole);
                }
                userSec.setRolesList(managedRoles);
            }

            // Guardamos el usuario en la base de datos
            return userSecRepository.save(userSec);

        } catch (Exception e) {
            e.printStackTrace(); // Muestra detalles del error en la consola
            throw new RuntimeException("Error al guardar el usuario: " + e.getMessage());
        }
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
        userSecRepository.save(userSec);// Guardamos en la base de datos

        return true; //
    }

    //agregamos el método encript password en UserService
    @Override
    public String encriptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }


}
