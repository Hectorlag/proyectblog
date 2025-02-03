package com.proyecto.blog.service;

import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IAuthorRepository;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService implements IAuthorService{

    @Autowired
    private IAuthorRepository authorRepository; // Repositorio de Author

    @Autowired
    private IUserSecRepository userSecRepository; // Repositorio de UserSec para asociar un UserSec al Author

    @Override
    public Author createAuthor(Author author, Long userSecId) {
        // Buscar el UserSec por el id proporcionado
        UserSec userSec = userSecRepository.findById(userSecId)
                .orElseThrow(() -> new RuntimeException("UserSec not found with id: " + userSecId));

        // Asociar el UserSec con el Author
        author.setUser(userSec);

        // Guardar el autor en la base de datos
        return authorRepository.save(author);
    }

    @Override
    public Optional<Author> getAuthorById(Long id) {
        // Buscar el autor por su id
        return authorRepository.findById(id);
    }

    @Override
    public List<Author> getAllAuthors() {
        // Obtener todos los autores desde la base de datos
        return authorRepository.findAll();
    }

    @Override
    public Author updateAuthor(Long id, Author authorDetails) {
        // Buscar el Author por id
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        // Actualizar los detalles del Author
        existingAuthor.setUser(authorDetails.getUser());
        existingAuthor.setPosts(authorDetails.getPosts());

        // Guardar el Author actualizado
        return authorRepository.save(existingAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        // Verificar si el Author existe antes de eliminarlo
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        // Eliminar el Author
        authorRepository.delete(author);
    }
}
