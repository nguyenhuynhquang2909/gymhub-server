package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.BannedMemberDTO;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.MemberResponseDTO;
import com.gymhub.gymhub.repository.InMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class MemberMapper {

    @Autowired
    private InMemoryRepository inMemoryRepository;

    public MemberResponseDTO memberToMemberResponseDTO(Member member) {
        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setId(member.getId());
        dto.setUserName(member.getUserName());
        dto.setEmail(member.getEmail());
        dto.setTitle(member.getTitle());
        dto.setBio(member.getBio());

        // Convert avatar from byte[] to Base64 and set it
        if (member.getAvatar() != null) {
            // Convert byte[] to Base64 string
            String base64Avatar = Base64.getEncoder().encodeToString(member.getAvatar());
            dto.setAvatar(base64Avatar); // Set the Base64-encoded string in the DTO
        }

        dto.setJoinDate(member.getJoinDate());
        dto.setLikeCount(inMemoryRepository.getMemberTotalLikeCountByMemberId(member.getId()));
        dto.setPostCount(inMemoryRepository.getMemberTotalPostCountByMemberId(member.getId()));
        dto.setFollowerIds(inMemoryRepository.getFollowersId(member.getId()));
        dto.setFollowingIds(inMemoryRepository.getFollowingId(member.getId()));
        dto.setFollowerCount(dto.getFollowerIds().size());
        dto.setFollowingCount(dto.getFollowingIds().size());
        dto.setBanUntilDate(inMemoryRepository.getBanUntilDateByMemberId(member.getId()));

        return dto;
    }

    public BannedMemberDTO memberToBannedMemberDTO(Member member, Long bannedUntil, String reason) {
        Date banUntilDate = new Date(bannedUntil);
        return new BannedMemberDTO(
                member.getId(),
                member.getUserName(),
                banUntilDate,
                reason
        );
    }
}
