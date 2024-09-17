package com.gymhub.gymhub.service;

import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.ModeratorRequestAndResponseDTO;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import com.gymhub.gymhub.repository.ModeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private InMemoryRepository inMemoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if username belongs to a moderator or member
        if (username.startsWith("mod_")) {
            return loadModeratorByUsername(username);
        } else {
            return loadMemberByUsername(username);
        }
    }

    // Load member and return CustomUserDetails
    private CustomUserDetails loadMemberByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Member Not Found with username: " + username));

        return buildUserDetails(
                member.getId(),
                member.getUserName(),
                member.getPassword(),
                inMemoryRepository.checkBanStatus(member.getId()), // Check if banned
                "ROLE_USER",
                null, // Set Member entity for custom user details
                member
        );
    }

    // Load moderator and return CustomUserDetails
    private CustomUserDetails loadModeratorByUsername(String username) throws UsernameNotFoundException {
        Moderator moderator = moderatorRepository.findModByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Moderator Not Found with username: " + username));

        return buildUserDetails(
                moderator.getId(),
                moderator.getUserName(),
                moderator.getPassword(),
                true, // Moderators are always enabled, but you can add custom logic
                "ROLE_MODERATOR",
                moderator, // Set Moderator entity for custom user details
                null
        );
    }

    // Helper method to build CustomUserDetails for both Member and Moderator
    private CustomUserDetails buildUserDetails(Long id, String username, String password, boolean isEnabled,
                                               String role, Moderator moderator, Member member) {
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(id);
        userDetails.setUsername(username);
        userDetails.setPassword(password);
        userDetails.setEnabled(isEnabled); // Based on ban status or other logic

        // Set authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        userDetails.setAuthorities(authorities);

        // Set entity based on the type of user
        if (moderator != null) {
            userDetails.setModerator(moderator);
        }
        if (member != null) {
            userDetails.setMember(member);
        }

        return userDetails;
    }
}




