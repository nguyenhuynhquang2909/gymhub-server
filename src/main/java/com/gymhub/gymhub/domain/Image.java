package com.gymhub.gymhub.domain;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Image {
    @Lob
    private byte[] image;

    public Image(byte[] image) {
        this.image = image;
    }
}
