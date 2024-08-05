package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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
@NamedEntityGraph(
        name = "Thread.author",
        includeAllAttributes = true,
        attributeNodes = @NamedAttributeNode("author")
)
public class Thread extends ForumUnit {

    @Column(name = "title", nullable = false, updatable = true)
    private String name;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    public Thread(String name, LocalDateTime creationDateTime) {
        super(creationDateTime);
        this.name = name;
    }
}
