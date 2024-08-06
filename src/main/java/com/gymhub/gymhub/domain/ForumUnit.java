package com.gymhub.gymhub.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long id;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDateTime;




    public ForumUnit(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;

    }
}