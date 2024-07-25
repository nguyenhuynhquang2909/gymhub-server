package com.gymhub.gymhub.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Post")
public class Post extends ForumUnit {
    @Column(name = "content", nullable = true, updatable = true)
    private String content;
    @ManyToOne
    @JoinColumn(name = "id")
    private Thread thread;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "id"))
    private List<Image> images;

    public Post() {

    }

    public Post(String name, LocalDateTime creationDate, String content, List<Image> images) {
        super(name, creationDate);
        this.content = content;
        this.images = images;
    }
}
