package com.proyecto.blog.service;

import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Post;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IPostRepository;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService implements IPostService{

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserSecRepository userSecRepository;

    // Crear un nuevo post
    @Override
    public Post createPost(Post post, Long userSecId) {
        UserSec userSec = userSecRepository.findById(userSecId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Author author = userSec.getAuthor();

        if (author != null) {
            post.setAuthor(author);
            return postRepository.save(post);
        } else {
            throw new RuntimeException("User does not have an author associated.");
        }
    }

    // Obtener un post por id
    @Override
    public Optional<Post> getPostById(Long id) {
        return postRepository.findByIdAndDeletedFalse(id);
    }

    // Obtener todos los posts
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findByDeletedFalse();
    }

    // Actualizar un post
    @Override
    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());

        return postRepository.save(post);
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
