package com.proyecto.blog.controller;

import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Post;
import com.proyecto.blog.service.IAuthorService;
import com.proyecto.blog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private IPostService postService;

    @Autowired
    private IAuthorService authorService;

    // Obtener todos los posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // Obtener un post por ID
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        // Buscar el Author real por ID
        Optional<Author> authorOptional = authorService.getAuthorEntityById(post.getAuthor().getId());

        if (authorOptional.isPresent()) {
            Author author = authorOptional.get(); // Obtener la entidad real

            post.setAuthor(author); // Asignamos la entidad Author real
            Post newPost = postService.createPost(post, author.getId());

            return ResponseEntity.ok(newPost);
        }

        return ResponseEntity.badRequest().build(); // Si el autor no existe, retornamos un bad request
    }



    // Actualizar un post
    @PatchMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Optional<Post> existingPost = postService.getPostById(id);
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());

            // Actualizamos el autor si es necesario
            if (postDetails.getAuthor() != null) {
                Author author = authorService.getAuthorEntityById(postDetails.getAuthor().getId())
                        .orElse(null);
                if (author != null) {
                    post.setAuthor(author);
                }
            }

            Post updatedPost = postService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
        }

        return ResponseEntity.notFound().build(); // Si el post no existe, retornamos not found
    }

    // Eliminar un post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
