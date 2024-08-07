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
public class Thread extends ForumUnit {

    @Column(name = "title", nullable = false, updatable = true)
    private String name;


    @Column(name = "category", nullable = false, updatable = true)
    private String category;


    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Member owner;


    public Thread(String name, LocalDateTime creationDateTime) {
        super(creationDateTime);
        this.name = name;
    }
}
