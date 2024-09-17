package com.gymhub.gymhub.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class ForumAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Add this to auto-generate the ID
    private Long id;

    @Column(name = "username", unique = true, nullable = false, updatable = true)
    private String userName;

    @Column(name = "password", nullable = false, updatable = true)
    private String password;

    @Column(name = "email", unique = true, nullable = false, updatable = true)
    private String email;

    public ForumAccount(Long id, String userName, String password, String email) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }
}