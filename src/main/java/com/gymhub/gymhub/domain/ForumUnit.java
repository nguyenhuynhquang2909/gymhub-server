package com.gymhub.gymhub.domain;
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
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entityCache")
@Cacheable
public abstract class ForumUnit {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Setter
    @Column(name = "title", nullable = false, updatable = true)
    private String name;

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
    private int likeCount;

    @Setter
    @Transient
    private int viewCount;

    @Setter
    @Transient
    private boolean beenReport;

    @Setter
    @Transient
    private boolean beenLiked;

    @Setter
    @Transient
    private String authorName;

    @Setter
    @Transient
    private byte[] authorAvatar;

    public ForumUnit(String name, LocalDateTime creationDateTime) {
        this.name = name;
        this.creationDateTime = creationDateTime;

    }
}