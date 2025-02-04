package com.proyecto.blog.service;

import com.proyecto.blog.model.Author;

import java.util.List;
import java.util.Optional;

public interface IAuthorService {

    Author createAuthor(Author author, Long userSecId); // Crear un nuevo autor
    Optional<Author> getAuthorById(Long id); // Obtener un autor por ID
    Author updateAuthor(Long id, Author authorDetails); // Actualizar un autor existente
    List<Author> findByDeletedFalse();    // Método para encontrar autores que no han sido eliminados
    Optional<Author> findByIdAndDeletedFalse(Long id);  // Método para buscar un autor por ID si no ha sido eliminado
}
