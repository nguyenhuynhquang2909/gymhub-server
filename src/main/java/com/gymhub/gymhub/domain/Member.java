package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "member")
@Schema(description = "Normal users of the forum")
public class Member extends ForumAccount {
    @Setter
    @Column(name = "title", nullable = false, length = 20, unique = false, updatable = true)
    private String title;

    @Setter
    @Column(name = "bio", nullable = true, length = 200, unique = false, updatable = true)
    private String bio;

    @Setter
    @JsonIgnore
    @Column(name = "avatar", nullable = true, updatable = true)
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

    public Member(Long memberId, String userName, String encode, String email, Date date) {
    }
}
