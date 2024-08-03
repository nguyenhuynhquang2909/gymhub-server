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
public class Post extends ForumUnit {

    @Column(name = "content", nullable = true, updatable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "post_id"))
    private List<Image> images;


    public Post(LocalDateTime creationDate, String content, List<Image> images) {
        super(creationDate);
        this.content = content;
        this.images = images;
    }
}
