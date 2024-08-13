package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.ModeratorRequestAndResponseDTO;
import com.gymhub.gymhub.repository.MemberRepository;
import com.gymhub.gymhub.repository.ModeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.startsWith("mod_")) {
            // If username starts with "mod_", it's a moderator
            return loadModeratorByUsername(username);
        } else {
            // Otherwise, it's a member
            return loadMemberByUsername(username);
        }
    }

    private UserDetails loadMemberByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Member Not Found with username: " + username));
        return User.builder()
                .username(member.getUserName())
                .password(member.getPassword())
                .roles("MEMBER")
                .build();
    }

    private UserDetails loadModeratorByUsername(String username) throws UsernameNotFoundException {
        Moderator mod = moderatorRepository.findModByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Moderator Not Found with username: " + username));
        return User.builder()
                .username(mod.getUserName())
                .password(mod.getPassword())
                .roles("MODERATOR")
                .build();
    }


}
