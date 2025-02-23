package com.proyecto.blog.controller;

import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.dto.PostDTOandNameAuthor;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Post;
import com.proyecto.blog.service.IAuthorService;
import com.proyecto.blog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
    @GetMapping
    public ResponseEntity<List<PostDTOandNameAuthor>> getAllPosts() {
        List<PostDTOandNameAuthor> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // Obtener un post por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PostDTOandNameAuthor> getPostById(@PathVariable Long id) {
        Optional<PostDTOandNameAuthor> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo post
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @PostMapping
    public ResponseEntity<PostDTOandNameAuthor> createPost(@RequestBody Post post) {
        PostDTOandNameAuthor newPostDTO = postService.createPost(post);
        return ResponseEntity.ok(newPostDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<PostDTOandNameAuthor> updatePost(
            @PathVariable Long id,
            @RequestBody Post postDetails,
            Authentication authentication) {

        PostDTOandNameAuthor updatedPost = postService.updatePost(id, postDetails, authentication);

        return ResponseEntity.ok(updatedPost);
    }
    // Eliminar un post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
