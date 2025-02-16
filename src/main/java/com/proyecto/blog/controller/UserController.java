package com.proyecto.blog.controller;

import com.proyecto.blog.dto.UserDTO;
import com.proyecto.blog.dto.UserSecResponseDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.service.IRoleService;
import com.proyecto.blog.service.IUserSecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    public ResponseEntity<UserSecResponseDTO> createUser(@RequestBody UserDTO userDTO,
                                                         @RequestParam boolean isAuthor,
                                                         String authorName) {
        UserSec newUser = userService.registerUser(userDTO, isAuthor, authorName);

        UserSecResponseDTO responseDTO = new UserSecResponseDTO(
                newUser.getUsername(),
                newUser.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()),
                newUser.getAuthor() != null ? newUser.getAuthor().getId() : null
        );

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserSecResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO,
            @RequestParam boolean isAuthor) {  //  Se recibe el parámetro en la URL

        UserSec updatedUser = userService.updateUserSec(id, userDTO, isAuthor);

        //  Convertir la entidad a DTO de respuesta
        UserSecResponseDTO responseDTO = new UserSecResponseDTO(
                updatedUser.getUsername(),
                updatedUser.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()),
                updatedUser.getAuthor() != null ? updatedUser.getAuthor().getId() : null
        );

        return ResponseEntity.ok(responseDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserSec(id);
        return ResponseEntity.noContent().build();
    }
}
