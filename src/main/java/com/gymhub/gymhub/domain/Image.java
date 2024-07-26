package com.gymhub.gymhub.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Image {
    private byte[] image;

    public Image(byte[] image) {
        this.image = image;
    }
}
