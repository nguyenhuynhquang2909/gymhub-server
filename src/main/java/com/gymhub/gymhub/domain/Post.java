package com.gymhub.gymhub.domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "Details unique to posts")
@Entity
@Table(name = "Post")
public class Post extends ForumUnit {

    @ApiModelProperty(value = "Content of the Post")
    @Column(name = "content", nullable = true, updatable = true)
    private String content;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id")
    private Thread thread;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<Image> images;

    @Transient
    @Setter
    @ApiModelProperty(value = "List of images of the post", notes = "encoded as a string in base64")
    private List<String> encodedImages = new ArrayList<>();

    @Transient
    @Setter
    @ApiModelProperty(value = "Id of the thread the post belongs to")
    private String threadId;

    public Post() {

    }

    public Post(LocalDateTime creationDate, String content, List<Image> images) {
        super(creationDate);
        this.content = content;
        this.images = images;
    }
}
