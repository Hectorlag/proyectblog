package com.proyecto.blog.controller;

import com.proyecto.blog.dto.UserDTO;
import com.proyecto.blog.dto.UserSecResponseDTO;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.service.IRoleService;
import com.proyecto.blog.service.IUserSecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private IUserSecService userService;

    @Autowired
    private IRoleService roleService;

    @GetMapping
    public ResponseEntity<List<UserSecResponseDTO>> getAllUsers() {
        List<UserSecResponseDTO> userList = userService.getAllUserSecs().stream()
                .map(user -> new UserSecResponseDTO(
                        user.getUsername(),
                        user.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()),
                        user.getAuthor() != null ? user.getAuthor().getId() : null
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSecResponseDTO> getUserById(@PathVariable Long id) {
        Optional<UserSec> user = userService.getUserSecById(id);

        return user.map(u -> ResponseEntity.ok(
                new UserSecResponseDTO(
                        u.getUsername(),
                        u.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()),
                        u.getAuthor() != null ? u.getAuthor().getId() : null
                )
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/user")
    public ResponseEntity<UserSecResponseDTO> createUser(@RequestBody UserDTO userDTO, @RequestParam boolean isAuthor) {
        UserSec newUser = userService.registerUser(userDTO, isAuthor);

        UserSecResponseDTO responseDTO = new UserSecResponseDTO(
                newUser.getUsername(),
                newUser.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()),
                newUser.getAuthor() != null ? newUser.getAuthor().getId() : null
        );

        return ResponseEntity.ok(responseDTO);
    }




    /*@PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede crear usuarios
    public ResponseEntity<UserSec> createUser(@RequestBody UserSec userSec) {
        Set<Role> roleList = new HashSet<>();
        Role readRole;

        // Encriptamos la contraseña antes de guardar
        userSec.setPassword(userService.encriptPassword(userSec.getPassword()));

        for (Role role : userSec.getRolesList()) {
            readRole = roleService.getRoleById(role.getId()).orElse(null);
            if (readRole != null) {
                roleList.add(readRole);
            }
        }

        if (!roleList.isEmpty()) {
            userSec.setRolesList(roleList);
            UserSec newUser = userService.createUserSec(userSec);
            return ResponseEntity.ok(newUser);
        }

        return ResponseEntity.badRequest().build();
    }*/

    @PatchMapping("/{id}")
    public ResponseEntity<UserSec> updateUser(@PathVariable Long id, @RequestBody UserSec userDetails) {
        UserSec existingUser = userService.getUserSecById(id).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        existingUser.setUsername(userDetails.getUsername());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(userService.encriptPassword(userDetails.getPassword()));
        }

        Set<Role> roleList = new HashSet<>();
        for (Role role : userDetails.getRolesList()) {
            Role readRole = roleService.getRoleById(role.getId()).orElse(null);
            if (readRole != null) {
                roleList.add(readRole);
            }
        }
        existingUser.setRolesList(roleList);

        UserSec updatedUser = userService.updateUserSec(id, existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserSec(id);
        return ResponseEntity.noContent().build();
    }
}
