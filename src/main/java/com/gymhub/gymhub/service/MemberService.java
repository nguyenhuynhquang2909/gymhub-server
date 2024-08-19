package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InMemoryRepository inMemoryRepository;

    @Autowired
    private Cache cache;





    public void followMember(Long followerId, Long followingId) {
        inMemoryRepository.follow(followerId, followingId);
    }

    public void unfollowMember(Long followerId, Long followingId) {
        inMemoryRepository.unfollow(followerId, followingId);
    }

    public Set<Long> getFollowersId(Long memberId) {
        return inMemoryRepository.getFollowersId(memberId);
   }


    public Set<Long> getFollowingId(Long memberId) {
       return inMemoryRepository.getFollowingId(memberId);
    }

    public int getFollowersNumber(Long memberId) {
        return inMemoryRepository.getFollowersNumber(memberId);
    }

    public int getFollowingNumber(Long memberId) {
        return  inMemoryRepository.getFollowingNumber(memberId);
    }


    public ResponseEntity<Void> updateMemberInfo(MemberRequestDTO memberRequestDTO) {
        Optional<Member> member =  memberRepository.findById(memberRequestDTO.getId());
        if (member.isPresent()) {
            Member existingMember = member.get();
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

    public Long getMemberIdFromUserName(String userName) {
        return memberRepository.findMemberByUserName(userName)
                .map(Member::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Additional business logic methods here
}
