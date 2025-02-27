package com.proyecto.blog.controller;

import com.proyecto.blog.dto.RoleRequestDTO;
import com.proyecto.blog.model.Permission;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.service.IPermissionService;
import com.proyecto.blog.service.IRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("api/roles")
public class RoleController {

    private final IRoleService roleService;
    private final IPermissionService permissionService;

    @Autowired
    public RoleController(IRoleService roleService, IPermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        if (roleRequestDTO.getRole() == null || roleRequestDTO.getRole().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del rol es obligatorio");
        }

        if (roleRequestDTO.getPermissionIds() == null || roleRequestDTO.getPermissionIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Debe asignar al menos un permiso");
        }

        Set<Permission> permissions = new HashSet<>();

        // Recuperar la lista de `Permission` por su id
        for (Long permissionId : roleRequestDTO.getPermissionIds()) {
            Permission foundPermission = permissionService.findPermissionEntityById(permissionId).orElse(null);
            if (foundPermission != null) {
                permissions.add(foundPermission);
            }
        }

        Role role = new Role();
        role.setRole(roleRequestDTO.getRole());
        role.setPermissionsList(permissions);

        Role newRole = roleService.createRole(role);
        return ResponseEntity.ok(newRole);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        Role existingRole = roleService.getRoleById(id).orElse(null);
        if (existingRole == null) {
            return ResponseEntity.notFound().build();
        }

        Set<Permission> permissions = new HashSet<>();

        // Recuperar la lista de `Permission` por su id
        for (Long permissionId : roleRequestDTO.getPermissionIds()) {
            Permission foundPermission = permissionService.findPermissionEntityById(permissionId).orElse(null);
            if (foundPermission != null) {
                permissions.add(foundPermission);
            }
        }

        existingRole.setRole(roleRequestDTO.getRole());
        existingRole.setPermissionsList(permissions);

        Role updatedRole = roleService.updateRole(id, existingRole);
        return ResponseEntity.ok(updatedRole);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
