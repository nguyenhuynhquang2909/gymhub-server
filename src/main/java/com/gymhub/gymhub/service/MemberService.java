package com.gymhub.gymhub.service;


import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.repository.MemberRepository;
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
public class MemberService implements UserDetailsService{
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return User.builder()
                .username(member.getUserName())
                .password(member.getPassword())
                .roles("USER")
                .build();
    }

    public ResponseEntity<Void> updateMemberInfo(MemberRequestDTO memberRequestDTO){
        //check if member exist
        Optional<Member> member = memberRepository.findById(memberRequestDTO.getId());
        if(member.isPresent()){
            Member existingMember = member.get();
            existingMember.setPassword(memberRequestDTO.getPassword());
            existingMember.setEmail(memberRequestDTO.getEmail());
            existingMember.setAvatar(memberRequestDTO.getStringAvatar().getBytes());
            existingMember.setBio(memberRequestDTO.getBio());
            memberRepository.save(existingMember);
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    
}
