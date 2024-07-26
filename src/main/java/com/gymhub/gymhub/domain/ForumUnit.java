package com.gymhub.gymhub.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class ForumUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Id of the post or thread", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDateTime;

    @Setter
    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonIgnore
    //This field will be excluded in the JSon conversion. Thus, it is marked by keyword "transient"
    private Member author;

    //The fields below are not mapped to a column in the table
    //They will be used when we want to convert the object into json
    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "like count of the thread / post", accessMode = Schema.AccessMode.READ_ONLY)
    private int likeCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "View count of the thread / post", accessMode = Schema.AccessMode.READ_ONLY)
    private int viewCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "True if the post / thread has been reported", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean beenReport;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "true if the post / thread has been liked by the user", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean beenLiked;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "name of the author of the post / thread", accessMode = Schema.AccessMode.READ_ONLY)
    private String authorName;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "id of the author of the post / thread", accessMode = Schema.AccessMode.READ_ONLY)
    private String authorId;


    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Encoded avatar (Base64) of the author of the post / thread", accessMode = Schema.AccessMode.READ_ONLY)
    private String authorAvatar;

    public ForumUnit(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;

    }
}