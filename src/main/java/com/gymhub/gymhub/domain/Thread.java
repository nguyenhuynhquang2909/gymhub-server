package com.gymhub.gymhub.domain;

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
@Table(name = "thread")
public class Thread extends ForumUnit {

    @Transient
    private int postCount;

    public Thread(String name, LocalDateTime creationDateTime) {
        super(name, creationDateTime);
    }
}
