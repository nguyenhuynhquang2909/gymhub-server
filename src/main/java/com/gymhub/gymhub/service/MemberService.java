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
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;



    public ResponseEntity<Void> updateMemberInfo(MemberRequestDTO memberRequestDTO) {
        // Check if member exists
        Optional<Member> member = memberRepository.findById(memberRequestDTO.getId());
        if (member.isPresent()) {
            Member existingMember = member.get();
            if (existingMember.getUserName().startsWith("mod")) {
                throw new IllegalArgumentException("Member username cannot start with 'mod'");
            }
            existingMember.setPassword(memberRequestDTO.getPassword());
            existingMember.setEmail(memberRequestDTO.getEmail());
            existingMember.setAvatar(memberRequestDTO.getStringAvatar().getBytes());
            existingMember.setBio(memberRequestDTO.getBio());
            memberRepository.save(existingMember);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //notifications method
}
