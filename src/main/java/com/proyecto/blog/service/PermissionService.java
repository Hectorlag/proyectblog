package com.proyecto.blog.service;

import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Permission;
import com.proyecto.blog.repository.IPermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService implements IPermissionService{

    @Autowired
    private IPermissionRepository permissionRepository;


    @Override
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findByIdAndDeletedFalse(id);
    }


    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findByDeletedFalse();
    }

    @Override
    public Permission updatePermission(Long id, Permission permissionDetails) {
        return permissionRepository.findByIdAndDeletedFalse(id).map(permission -> {
            permission.setPermissionName(permissionDetails.getPermissionName()); // Actualiza el nombre del permiso
            return permissionRepository.save(permission);
        }).orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));
    }

    @Override
    public boolean deletePermission(Long id) {
        Permission permission = permissionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        permission.setDeleted(true); // Marcamos el permission como eliminado
        permissionRepository.save(permission); // Guardamos el cambio en la base de datos

        return true; // Indicamos que la operación fue exitosa
    }



}

