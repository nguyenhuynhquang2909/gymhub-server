package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "member")
@Schema(description = "Normal users of the forum")
public class Member extends ForumAccount {
    @Setter
    @Column(name = "title", nullable = true, length = 20, unique = false)
    private String title;

    @Setter
    @Column(name = "bio", nullable = true, length = 200, unique = false)
    private String bio;

    @Setter
    @JsonIgnore
    @Column(name = "avatar", nullable = true, updatable = false)
    private byte[] avatar;

    @Setter
    @Transient
    private String stringAvatar;

    @Setter
    @Column(name = "join_date", nullable = false, updatable = false, unique = false)
    private Date joinDate;

    @Transient
    private Set<Long> followers = new HashSet<>();

    @Transient
    private Set<Long> following = new HashSet<>();


    public Member(String userName, String password, String email, Date joinDate) {
        super(userName, password, email);
        this.joinDate = joinDate;
    }

    public void follow(Long memeberId) {
        following.add(memeberId);
    }
    public void unfollow(Long memberId) {
        following.remove(memberId);
    }
    public void addFollower(Long memberId) {
        followers.add(memberId);
    }
    public void removeFollower(Long memberId) {
        followers.remove(memberId);
    }

}
