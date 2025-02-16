package com.proyecto.blog.controller;

import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.dto.PostDTOandNameAuthor;
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
    public ResponseEntity<List<PostDTOandNameAuthor>> getAllPosts() {
        List<PostDTOandNameAuthor> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // Obtener un post por ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDTOandNameAuthor> getPostById(@PathVariable Long id) {
        Optional<PostDTOandNameAuthor> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo post
    @PostMapping
    public ResponseEntity<PostDTOandNameAuthor> createPost(@RequestBody Post post) {
        PostDTOandNameAuthor newPostDTO = postService.createPost(post);
        return ResponseEntity.ok(newPostDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDTOandNameAuthor> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        // Llamamos al servicio para actualizar el post
        PostDTOandNameAuthor updatedPost = postService.updatePost(id, postDetails);

        return ResponseEntity.ok(updatedPost); // Retornamos el DTO con el post actualizado
    }

    // Eliminar un post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
