package com.gymhub.gymhub.domain;

import jakarta.persistence.Entity;

@Entity
public class Moderator extends ForumAccount {
    public Moderator() {
    }

    public Moderator(String userName, String password, String email) {
        super(userName, password, email);
    }
}