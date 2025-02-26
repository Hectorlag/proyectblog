package com.proyecto.blog.controller;

import com.proyecto.blog.dto.RoleRequestDTO;
import com.proyecto.blog.dto.RoleResponseDTO;
import com.proyecto.blog.model.Permission;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.service.IPermissionService;
import com.proyecto.blog.service.IRoleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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
@RequestMapping("api/roles")
public class RoleController {

    private final IRoleService roleService;
    private final IPermissionService permissionService;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleController(IRoleService roleService, IPermissionService permissionService, ModelMapper modelMapper) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleResponseDTO> roleDTOs = roles.stream()
                .map(role -> modelMapper.map(role, RoleResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(r -> ResponseEntity.ok(modelMapper.map(r, RoleResponseDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO roleRequest) {
        Set<Permission> permissions = roleRequest.getPermissionIds().stream()
                .map(permissionService::findPermissionEntityById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Role role = new Role();
        role.setRole(roleRequest.getRole());
        role.setPermissionsList(permissions);

        Role newRole = roleService.createRole(role);
        return ResponseEntity.ok(modelMapper.map(newRole, RoleResponseDTO.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDTO roleRequest) {
        Role existingRole = roleService.getRoleById(id).orElse(null);
        if (existingRole == null) {
            return ResponseEntity.notFound().build();
        }

        existingRole.setRole(roleRequest.getRole());
        Set<Permission> permissions = roleRequest.getPermissionIds().stream()
                .map(permissionService::findPermissionEntityById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        existingRole.setPermissionsList(permissions);

        Role updatedRole = roleService.updateRole(id, existingRole);
        return ResponseEntity.ok(modelMapper.map(updatedRole, RoleResponseDTO.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}

