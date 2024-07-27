package com.gymhub.gymhub.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Details unique to posts")
@Entity
@Table(name = "Post")
public class Post extends ForumUnit {

    @Schema(description = "Content of the Post")
    @Column(name = "content", nullable = true, updatable = true)
    private String content;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "post_id"))
    private List<Image> images;


    @Transient
    @Setter
    @Schema(description = "Id of the thread the post belongs to", accessMode = Schema.AccessMode.READ_ONLY)
    private String threadId;

    public Post() {

    }

    public Post(LocalDateTime creationDate, String content, List<Image> images) {
        super(creationDate);
        this.content = content;
        this.images = images;
    }
}
