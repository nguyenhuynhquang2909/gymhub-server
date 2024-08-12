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

    @Column(name = "image", nullable = false)
    private String encodedImage;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false) // Ensure the post_id column exists in your images table
    private Post post;

    public Image(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public Image(byte[] bytes) {
    }

    public String getEncodedImage() {
        return encodedImage;
    }
}