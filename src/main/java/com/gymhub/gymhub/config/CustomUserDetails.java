package com.gymhub.gymhub.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private boolean isEnabled;
    private Collection<? extends GrantedAuthority> authorities;
}
