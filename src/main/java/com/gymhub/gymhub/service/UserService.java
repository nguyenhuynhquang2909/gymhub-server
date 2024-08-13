package com.gymhub.gymhub.service;

import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private Cache cache;

    public void getUserInfo(String username){
        System.out.println("Info of "+username);
        memberRepository.findMemberByUserName(username);
    }
}
;