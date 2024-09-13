package com.gymhub.gymhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Post")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Post.author",
                attributeNodes = @NamedAttributeNode("author"),
                includeAllAttributes = true
        ),
        @NamedEntityGraph(
                name = "Post.images",
                attributeNodes = @NamedAttributeNode("images"),
                includeAllAttributes = true
        ),
        @NamedEntityGraph(
                name = "Post.full", // Fetch both author and images
                attributeNodes = {
                        @NamedAttributeNode("author"),
                        @NamedAttributeNode("images")
                }
        )
})
public class Post extends ForumUnit {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content", nullable = false, updatable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false, updatable = true)
    private Thread thread;

    @Setter
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = true)
    private Member author;

    // Ensure that the image can be null by setting optional = true
    @OneToMany(mappedBy = "post")
    private Collection<Image> images;


    public Post(LocalDateTime creationDate, String content, Collection<Image> images) {
        super(creationDate);
        this.content = content;
        this.images = images;
    }

    public Post(LocalDateTime creationDate, String content, Collection<Image> images, Member author, Thread thread) {
        super(creationDate);
        this.content = content;
        this.images = images;
        this.author = author;
        this.thread = thread;
    }
    public Post(Long id, LocalDateTime creationDate, String content, Collection<Image> images, Member author, Thread thread) {
        super(creationDate);
        this.id = id; // Manually assign the ID using PostSequence
        this.content = content;
        this.images = images;
        this.author = author;
        this.thread = thread;
    }
    public Post(Long id, LocalDateTime creationDate, String content, Member author, Thread thread) {
        super(creationDate);
        this.id = id; // Manually assign the ID using PostSequence
        this.content = content;
        this.images = images;
        this.author = author;
        this.thread = thread;
    }

}
