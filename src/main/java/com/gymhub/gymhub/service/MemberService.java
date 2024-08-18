package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InMemoryRepository inMemoryRepository;

    public void updateMemberInfo(MemberRequestDTO memberRequestDTO) {
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
        } else {
            throw new IllegalArgumentException("Member not found with id: " + memberRequestDTO.getId());
        }
    }

    public void followAnotherMember(Long followerId, Long followingId) {
        inMemoryRepository.follow(followerId, followingId);
    }

    public void unfollowAnotherMember(Long followerId, Long followingId) {
        inMemoryRepository.unfollow(followerId, followingId);
    }

    // Additional business logic methods here
}
