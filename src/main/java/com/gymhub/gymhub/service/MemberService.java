package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.MemberResponseDTO;
import com.gymhub.gymhub.dto.UpdateMemberPreviewResponseDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.MemberMapper;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
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
    @Autowired
    private MemberMapper memberMapper;





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


    public ResponseEntity<Void> updateMemberInfo(Long memberId, MemberRequestDTO memberRequestDTO) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isPresent()) {
            Member existingMember = member.get();

            // Set the new values from the DTO
            existingMember.setPassword(memberRequestDTO.getPassword());
            existingMember.setEmail(memberRequestDTO.getEmail());
            existingMember.setBio(memberRequestDTO.getBio());

            // Handle avatar conversion from Base64 string to byte[]
            if (memberRequestDTO.getStringAvatar() != null && !memberRequestDTO.getStringAvatar().isEmpty()) {
                byte[] decodedAvatar = Base64.getDecoder().decode(memberRequestDTO.getStringAvatar()); // Convert Base64 string to byte[]
                existingMember.setAvatar(decodedAvatar); // Set the decoded avatar byte[] in the Member entity
            }

            // Save the updated member in the repository
            memberRepository.save(existingMember);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public UpdateMemberPreviewResponseDTO displayMemberUpdatePreview(Long memberId){
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isPresent()) {
            Member existingMember = member.get();
            UpdateMemberPreviewResponseDTO updateMemberPreviewResponseDTO = memberMapper.memberToMemberUpdatePreviewDTO(existingMember);
            return updateMemberPreviewResponseDTO;
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Long getMemberIdFromUserName(String userName) {
        return memberRepository.findMemberByUserName(userName)
                .map(Member::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Additional business logic methods here
}
