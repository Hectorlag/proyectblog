package com.proyecto.blog.controller;

import com.proyecto.blog.dto.UserDTO;
import com.proyecto.blog.dto.UserSecResponseDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.service.IRoleService;
import com.proyecto.blog.service.IUserSecService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private IUserSecService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<String> getAdminStatus() {
        return ResponseEntity.ok("🔐 Acceso concedido: Estás autenticado como ADMIN y tienes acceso a los recursos protegidos.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserSecResponseDTO>> getAllUsers() {
        List<UserSecResponseDTO> userList = userService.getAllUserSecs().stream()
                .map(UserSecResponseDTO::fromUserSec)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserSecResponseDTO> getUserById(@PathVariable Long id) {
        UserSec user = userService.getUserSecById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return ResponseEntity.ok(UserSecResponseDTO.fromUserSec(user));
    }

    @PostMapping
    public ResponseEntity<UserSecResponseDTO> createUser(
            @Valid @RequestBody UserDTO userDTO,
            @RequestParam boolean isAuthor,
            @RequestParam(required = false) String authorName) {

        UserSec newUser = userService.registerUser(userDTO, isAuthor, authorName);
        return ResponseEntity.ok(UserSecResponseDTO.fromUserSec(newUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserSecResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO,
            @RequestParam(required = false, defaultValue = "false") boolean isAuthor,
            @RequestParam(required = false) String authorName) {

        UserSec updatedUser = userService.updateUserSec(id, userDTO, isAuthor, authorName);
        return ResponseEntity.ok(UserSecResponseDTO.fromUserSec(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserSec(id);
        return ResponseEntity.noContent().build();
    }

    //Método de utilidad
    public static UserSecResponseDTO fromUserSec(UserSec user) {
        return new UserSecResponseDTO(
                user.getUsername(),
                user.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()),
                user.getAuthor() != null ? user.getAuthor().getId() : null
        );
    }

}
