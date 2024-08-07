package com.gymhub.gymhub.service;

import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Cache cache;

    public void getUserInfo(String username){
        System.out.println("Info of "+username);
        userRepository.findByUserName(username);
    }
}
;