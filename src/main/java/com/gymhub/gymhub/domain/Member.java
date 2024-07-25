package com.gymhub.gymhub.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Member extends ForumAccount {
    @Setter
    @Column(name = "title", nullable = true, length = 20, unique = false)
    private String title;

    @Setter
    @Column(name = "bio", nullable = true, length = 200, unique = false)
    private String bio;

    @Setter
    @Lob
    @Column(name = "avatar", nullable = true, updatable = false)
    private byte[] avatar;

    @Setter
    @Column(name = "join_date", nullable = false, updatable = false, unique = false)
    private Date joinDate;

    @Setter
    @Transient
    private LocalDateTime lastSeen;

    @Setter
    @Transient
    private int likeCount;

    @Setter
    @Transient
    private int postCount;

    @Setter
    @Transient
    private int followerCount;


    public Member(String userName, String password, String email, Date joinDate) {
        super(userName, password, email);
        this.joinDate = joinDate;
    }
}
