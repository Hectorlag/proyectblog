package com.proyecto.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author; // Relación con Author

}
