package com.proyecto.blog.controller;


import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.service.IAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

    @RestController
    @RequestMapping("api/authors")
    public class AuthorController {

        @Autowired
        private IAuthorService authorService; // Asegúrate de que tienes el servicio adecuado

        // Obtener todos los autores
        @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
        @GetMapping
        public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
            List<AuthorDTO> authors = authorService.getAllAuthors();
            return ResponseEntity.ok(authors);
        }

        // Obtener autor por ID
        @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
        @GetMapping("/{id}")
        public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
            Optional<AuthorDTO> author = authorService.getAuthorById(id);
            return author.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

       // Crear un nuevo autor a partir de un usuario existente
       // CREATE (Solo ADMIN puede crear autores a partir de usuarios)
       @PreAuthorize("hasRole('ADMIN')")
       @PostMapping("/{userId}")
       public ResponseEntity<AuthorDTO> createAuthor(@PathVariable Long userId) {
            AuthorDTO newAuthor = authorService.createAuthor(userId);

               return ResponseEntity.status(HttpStatus.CREATED).body(newAuthor);
           }

        // Actualizar autor existente
        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/{id}")
        public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody Author authorDetails) {
            AuthorDTO updatedAuthor = authorService.updateAuthor(id, authorDetails);
            if (updatedAuthor != null) {
                return ResponseEntity.ok(updatedAuthor);
            }
            return ResponseEntity.notFound().build();
        }

        // Eliminar autor
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
            authorService.deleteAuthor(id);
            return ResponseEntity.noContent().build();
        }

        @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
        @GetMapping("/status")
        public ResponseEntity<String> getAuthorAccessStatus(Authentication authentication) {
            String role = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .filter(authority -> authority.startsWith("ROLE_")) // Filtra solo roles
                    .map(roleName -> roleName.replace("ROLE_", "")) // Elimina el prefijo "ROLE_"
                    .findFirst()
                    .orElse("Sin rol asignado");

            return ResponseEntity.ok("✅ Estás autenticado como: " + role);
        }




    }
