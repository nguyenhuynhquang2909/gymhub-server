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
        name = "Thread.owner",
        attributeNodes = @NamedAttributeNode("owner")
)

public class Thread extends ForumUnit {

    @Column(name = "title", nullable = false, updatable = true)
    private String title;


    @Column(name = "category", nullable = false, updatable = true)
    private String category;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private Member owner;




    public Thread(String name, LocalDateTime creationDateTime) {
        super(creationDateTime);
        this.title = name;
    }

    public Thread(LocalDateTime now, String title) {
        super(now);
        this.title = title;
    }

    public Thread(long id, String title,String category, LocalDateTime creationDateTime) {
        super(creationDateTime);
        this.setId(id);  // Sets the ID explicitly
        this.title = title;
        this.category = category;
    }


}
