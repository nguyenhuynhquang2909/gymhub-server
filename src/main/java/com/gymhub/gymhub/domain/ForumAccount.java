package com.gymhub.gymhub.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@ApiModel(value = "The common attributes of all account type")
public class ForumAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The id of an account")
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, updatable = true)
    @Schema(description = "The username of an account")
    private String userName;

    @Column(name = "password", nullable = false, updatable = true)
    @Schema(description = "The password of an account")
    private String password;

    @Schema(description = "The email of an account")
    @Column(name = "email", unique = true, nullable = false, updatable = true)
    private String email;

    public ForumAccount(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }
}