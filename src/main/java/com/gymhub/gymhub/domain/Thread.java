package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Thread")
@Schema(description = "Details unique to threads")
public class Thread extends ForumUnit {
    @Column(name = "title", nullable = false, updatable = true)
    @ApiModelProperty(value = "The title of the thread")
    private String name;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "The encoded avatar of the author of the post or thread")
    private int postCount;

    public Thread(String name, LocalDateTime creationDateTime) {
        super(creationDateTime);
        this.name = name;
    }
}
