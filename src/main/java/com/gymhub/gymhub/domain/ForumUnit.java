package com.gymhub.gymhub.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class ForumUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "Id of the post or thread")
    private Long id;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDateTime;

    @Setter
    @ManyToOne
    @JoinColumn(name = "id")
    @JsonIgnore
    //This field will be excluded in the JSon conversion. Thus, it is marked by keyword "transient"
    private Member author;

    //The fields below are not mapped to a column in the table
    //They will be used when we want to convert the object into json
    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "The like count of the post or thread")
    private int likeCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "The view count of the post or thread")
    private int viewCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(
            value = "The report status of the post or thread",
            notes = "true if it has been reported" )
    private boolean beenReport;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(
            value = "The like status of the post or thread",
            notes = "true if it has been liked" )
    private boolean beenLiked;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "The name of the author of the post or thread")
    private String authorName;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "The id of the author of the post or thread")
    private String authorId;


    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "The encoded avatar of the author of the post or thread")
    private String authorAvatar;

    public ForumUnit(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;

    }
}