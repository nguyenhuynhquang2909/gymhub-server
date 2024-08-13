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

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false, unique = true) // Ensures post_id is unique
    private Post post;

    public Image(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public Image(byte[] bytes) {
        this.encodedImage = new String(bytes); // Converts bytes to String
    }

    public String getEncodedImage() {
        return encodedImage;
    }
}