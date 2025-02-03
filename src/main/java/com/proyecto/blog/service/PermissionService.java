package com.proyecto.blog.service;

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
        return permissionRepository.findById(id);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission updatePermission(Long id, Permission permissionDetails) {
        return permissionRepository.findById(id).map(permission -> {
            permission.setPermissionName(permissionDetails.getPermissionName()); // Actualiza el nombre del permiso
            return permissionRepository.save(permission);
        }).orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }


}

