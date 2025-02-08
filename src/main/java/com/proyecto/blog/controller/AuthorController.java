package com.proyecto.blog.controller;


import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.service.IAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

    @RestController
    @RequestMapping("api/authors")
    public class AuthorController {

        @Autowired
        private IAuthorService authorService; // Asegúrate de que tienes el servicio adecuado

        // Obtener todos los autores
        @GetMapping
        public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
            List<AuthorDTO> authors = authorService.getAllAuthors();
            return ResponseEntity.ok(authors);
        }

        // Obtener autor por ID
        @GetMapping("/{id}")
        public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
            Optional<AuthorDTO> author = authorService.getAuthorById(id);
            return author.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

       // Crear un nuevo autor a partir de un usuario existente
           

        // Actualizar autor existente
        @PatchMapping("/{id}")
        public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody Author authorDetails) {
            AuthorDTO updatedAuthor = authorService.updateAuthor(id, authorDetails);
            if (updatedAuthor != null) {
                return ResponseEntity.ok(updatedAuthor);
            }
            return ResponseEntity.notFound().build();
        }

        // Eliminar autor
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
            authorService.deleteAuthor(id);
            return ResponseEntity.noContent().build();
        }


}
