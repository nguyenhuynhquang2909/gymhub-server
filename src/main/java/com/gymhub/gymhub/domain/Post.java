package com.gymhub.gymhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Post")
@NamedEntityGraph(
        name = "Post.author",
        includeAllAttributes = true,
        attributeNodes = @NamedAttributeNode("author")
)
public class Post extends ForumUnit {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content", nullable = false, updatable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false, updatable = false)
    private Thread thread;

    @Setter
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private Member author;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;

    public Post(LocalDateTime creationDate, String content, Image image) {
        super(creationDate);
        this.content = content;
        this.image = image;
    }

    public Post(LocalDateTime creationDate, String content, Image image, Member author, Thread thread) {
        super(creationDate);
        this.content = content;
        this.image = image;
        this.author = author;
        this.thread = thread;
    }
    public Post(Long id, LocalDateTime creationDate, String content, Image image, Member author, Thread thread) {
        super(creationDate);
        this.id = id; // Manually assign the ID using PostSequence
        this.content = content;
        this.image = image;
        this.author = author;
        this.thread = thread;
    }

}
