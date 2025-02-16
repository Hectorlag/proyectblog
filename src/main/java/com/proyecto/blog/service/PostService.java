package com.proyecto.blog.service;

import com.proyecto.blog.dto.PostDTOandNameAuthor;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Post;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IAuthorRepository;
import com.proyecto.blog.repository.IPostRepository;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService implements IPostService{

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IAuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    // Crear un nuevo post
    @Override
    public PostDTOandNameAuthor createPost(Post post) {
        // Buscar el Author real por ID
        Author author = authorService.getAuthorEntityById(post.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        post.setAuthor(author); // Asignamos la entidad Author real

        // Guardamos el nuevo post
        Post savedPost = postRepository.save(post);

        // Retornamos el DTO con el título, contenido y nombre del autor
        return new PostDTOandNameAuthor(savedPost.getTitle(), savedPost.getContent(), savedPost.getAuthor().getUser().getUsername());
    }


    // Obtener un post por id
    @Override
    // Obtener un post por ID y devolver en formato DTO
    public Optional<PostDTOandNameAuthor> getPostById(Long id) {
        return postRepository.findByIdAndDeletedFalse(id)
                .map(post -> new PostDTOandNameAuthor(
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor().getUser().getUsername()));
    }

    // Obtener todos los posts en formato DTO
    @Override
    public List<PostDTOandNameAuthor> getAllPosts() {
        return postRepository.findByDeletedFalse().stream()
                .map(post -> new PostDTOandNameAuthor(
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor().getUser().getUsername()))
                .collect(Collectors.toList());
    }
    // Actualizar un post
    @Override
    public PostDTOandNameAuthor updatePost(Long id, Post postDetails) {
        // Buscar el post con id y que no esté eliminado
        Post post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Actualizar el título y contenido
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());

        // Si el autor se pasa, actualizarlo
        if (postDetails.getAuthor() != null) {
            Author author = authorService.getAuthorEntityById(postDetails.getAuthor().getId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            post.setAuthor(author);
        }

        // Guardar el post actualizado
        Post updatedPost = postRepository.save(post);

        // Retornar el DTO con el título, contenido y nombre del autor
        return new PostDTOandNameAuthor(updatedPost.getTitle(), updatedPost.getContent(), updatedPost.getAuthor().getUser().getUsername());
    }

    // Eliminar un post

    @Override
    public boolean deletePost(Long id) {
        Post post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setDeleted(true); // Marcamos el post como eliminado
        postRepository.save(post); // Guardamos el cambio en la base de datos

        return true; // Indicamos que la operación fue exitosa
    }

}
