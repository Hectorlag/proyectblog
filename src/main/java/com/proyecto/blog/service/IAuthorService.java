package com.proyecto.blog.service;

import com.proyecto.blog.model.Author;

import java.util.List;
import java.util.Optional;

public interface IAuthorService {

    Author createAuthor(Author author, Long userSecId); // Crear un nuevo autor
    Optional<Author> getAuthorById(Long id); // Obtener un autor por ID
    List<Author> getAllAuthors(); // Obtener todos los autores
    Author updateAuthor(Long id, Author authorDetails); // Actualizar un autor existente
    void deleteAuthor(Long id); // Eliminar un autor
}
