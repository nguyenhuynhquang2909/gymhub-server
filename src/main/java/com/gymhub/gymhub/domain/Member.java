package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Member")
@Schema(description = "Normal users of the forum")
public class Member extends ForumAccount {
    @Setter
    @Column(name = "title", nullable = true, length = 20, unique = false)
    private String title;

    @Setter
    @Column(name = "bio", nullable = true, length = 200, unique = false)
    private String bio;

    @Setter
    @Lob
    @JsonIgnore
    @Column(name = "avatar", nullable = true, updatable = false)
    private byte[] avatar;

    @Setter
    @Transient
    private String stringAvatar;

    @Setter
    @Column(name = "join_date", nullable = false, updatable = false, unique = false)
    private Date joinDate;

    public Member(String userName, String password, String email, Date joinDate) {
        super(userName, password, email);
        this.joinDate = joinDate;
    }
}
