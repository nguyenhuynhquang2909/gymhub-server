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
    private Long id;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDateTime;




    public ForumUnit(Long id, LocalDateTime creationDateTime) {
        this.id = id;
        this.creationDateTime = creationDateTime;

    }
}