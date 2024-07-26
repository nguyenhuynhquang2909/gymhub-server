package com.gymhub.gymhub.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "The id of an account")
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, updatable = true)
    @ApiModelProperty(value = "The username of an account")
    private String userName;

    @Column(name = "password", nullable = false, updatable = true)
    @ApiModelProperty(value = "The password of an account")
    private String password;

    @ApiModelProperty(value = "The email of an account")
    @Column(name = "email", unique = true, nullable = false, updatable = true)
    private String email;

    public ForumAccount(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }
}