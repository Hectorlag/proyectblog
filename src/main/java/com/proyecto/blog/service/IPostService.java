package com.proyecto.blog.service;

import com.proyecto.blog.model.Post;

import java.util.List;
import java.util.Optional;

public interface IPostService {

    Post createPost(Post post, Long userSecId);
    Optional getPostById(Long id);
    List<Post> getAllPosts();
    Post updatePost(Long id, Post postDetails);
    void deletePost(Long id);
}
