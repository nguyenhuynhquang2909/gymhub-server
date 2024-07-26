package com.gymhub.gymhub.domain;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@ApiModel(value = "Details unique to moderators")
@Table(name = "Moderator")
public class Moderator extends ForumAccount {
    public Moderator() {
    }

    public Moderator(String userName, String password, String email) {
        super(userName, password, email);
    }
}