package com.proyecto.blog.service;

import com.proyecto.blog.dto.PostDTOandNameAuthor;
import com.proyecto.blog.model.Post;

import java.util.List;
import java.util.Optional;

public interface IPostService {

    PostDTOandNameAuthor createPost(Post post);
    Optional<PostDTOandNameAuthor> getPostById(Long id);
    List<PostDTOandNameAuthor> getAllPosts();
    PostDTOandNameAuthor updatePost(Long id, Post postDetails);
    boolean deletePost(Long id);
}
