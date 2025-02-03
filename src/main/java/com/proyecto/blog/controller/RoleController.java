package com.proyecto.blog.controller;

import com.proyecto.blog.model.Permission;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.service.IPermissionService;
import com.proyecto.blog.service.IRoleService;
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

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Set<Permission> permissions = new HashSet<>();

        // Recuperar la lista de `Permission` por su id
        for (Permission p : role.getPermissionsList()) {
            Permission foundPermission = permissionService.getPermissionById(p.getId()).orElse(null);
            if (foundPermission != null) {
                permissions.add(foundPermission);
            }
        }

        role.setPermissionsList(permissions);
        Role newRole = roleService.createRole(role);
        return ResponseEntity.ok(newRole);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
        Role existingRole = roleService.getRoleById(id).orElse(null);
        if (existingRole == null) {
            return ResponseEntity.notFound().build();
        }

        existingRole.setRole(roleDetails.getRole());
        existingRole.setPermissionsList(roleDetails.getPermissionsList());

        Role updatedRole = roleService.updateRole(id, existingRole);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
