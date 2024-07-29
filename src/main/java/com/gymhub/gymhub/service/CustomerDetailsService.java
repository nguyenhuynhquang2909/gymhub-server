package com.gymhub.gymhub.service;


import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gymhub.gymhub.domain.ForumAccount;


@Service
public class CustomerDetailsService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = userRepository.findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return User.builder()
                .username(member.getUserName())
                .password(member.getPassword())
                .roles("USER")
                .build();
    }

    
}
