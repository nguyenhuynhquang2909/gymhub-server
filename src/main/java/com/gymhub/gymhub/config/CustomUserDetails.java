package com.gymhub.gymhub.config;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Moderator;
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

    // Add the Member field
    private Member member;

    // Method to get the Member entity
    public Member getMember() {
        return this.member;
    }


    // Add the Mod field
    private Moderator moderator;

    // Method to get the Mod entity


    public Moderator getModerator() {
        return moderator;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;  // This should return true if the user is enabled
    }

    // Implement other required methods from UserDetails interface
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
