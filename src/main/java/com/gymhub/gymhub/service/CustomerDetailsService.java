package com.gymhub.gymhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gymhub.gymhub.domain.ForumAccount;
import com.gymhub.gymhub.repository.ForumAccountRepository;

@Service
public class CustomerDetailsService implements UserDetailsService{
    @Autowired
    private ForumAccountRepository forumAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumAccount forumAccount = forumAccountRepository.findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return User.builder()
                .username(forumAccount.getUserName())
                .password(forumAccount.getPassword())
                .roles("USER")
                .build();
    }

    
}
