package com.proyecto.blog.service;


import com.proyecto.blog.model.Permission;

import java.util.List;
import java.util.Optional;

public interface IPermissionService {
        Permission createPermission(Permission permission);
        Optional<Permission> getPermissionById(Long id);
        List<Permission> getAllPermissions();
        Permission updatePermission(Long id, Permission permissionDetails);
        void deletePermission(Long id);

}
