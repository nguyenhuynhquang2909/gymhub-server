package com.gymhub.gymhub.domain;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Details unique to moderators")
@Table(name = "Moderator")
public class Moderator extends ForumAccount {
    public Moderator(String userName, String password, String email) {
        super( userName, password, email);
    }


}