package com.gymhub.gymhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image {
    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image", nullable = true, updatable = true, columnDefinition="bytea")
    private byte[] encodedImage;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true, updatable = false) // Ensures post_id is unique
    private Post post;

    public Image(Long id, byte[] encodedImage, Post post) {
        this.id = id;
        this.encodedImage = encodedImage;
        this.post = post;
    }
}