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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;


    @Column(name = "image", nullable = false, updatable = true)
    @Lob // Use @Lob for large binary data
    private byte[] encodedImage;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false, unique = true, updatable = false) // Ensures post_id is unique
    private Post post;

    public Image(byte[] encodedImage) {
        this.encodedImage = encodedImage;
    }

    public Image(Long id, byte[] encodedImage, Post post) {
        this.id = id;
        this.encodedImage = encodedImage;
        this.post = post;
    }
}