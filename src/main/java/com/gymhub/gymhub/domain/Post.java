package com.gymhub.gymhub.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @Column(name = "content", nullable = true, updatable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Setter
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    public Post(Long id, LocalDateTime creationDate, String content, List<Image> images) {
        super(id, creationDate);
        this.content = content;
        this.images = images;
    }
    public Post(LocalDateTime creationDate, String content, List<Image> images) {
        super(null, creationDate);  // Pass null for the id
        this.content = content;
        this.images = images;
    }
}
