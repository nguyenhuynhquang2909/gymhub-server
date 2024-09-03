package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "thread")
@Schema(description = "Details unique to threads")
@NamedEntityGraph(
        name = "Thread.owner",
        attributeNodes = @NamedAttributeNode("owner")
)
public class Thread extends ForumUnit {

    @Column(name = "title", nullable = false, updatable = true)
    private String title;

    @Enumerated(EnumType.STRING)  // Ensure that the enum is stored as a String in the database
    @Column(name = "category", nullable = false, updatable = true)
    private ThreadCategoryEnum category;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private Member owner;

    @ManyToMany
    @JoinTable(
            name = "thread_tag",  // Match the table name with your database schema
            joinColumns = @JoinColumn(name = "thread_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Thread(String name, LocalDateTime creationDateTime) {
        super(creationDateTime);
        this.title = name;
    }

    public Thread(LocalDateTime creationDateTime, String title) {
        super(creationDateTime);
        this.title = title;
    }

    public Thread(long id, String title, ThreadCategoryEnum category, LocalDateTime creationDateTime) {
        super(creationDateTime);
        this.setId(id);  // Sets the ID explicitly
        this.title = title;
        this.category = category;
    }


    public Thread(Long id, String title, ThreadCategoryEnum category, LocalDateTime creationDateTime, Set<Tag> tags) {
        super(id, creationDateTime);
        this.title = title;
        this.category = category;
        this.owner = owner;
        this.tags = tags;
    }
}
