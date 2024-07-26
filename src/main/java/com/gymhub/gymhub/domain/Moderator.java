package com.gymhub.gymhub.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Schema(description = "Details unique to moderators")
@Table(name = "Moderator")
public class Moderator extends ForumAccount {
    public Moderator() {
    }

    public Moderator(String userName, String password, String email) {
        super(userName, password, email);
    }
}