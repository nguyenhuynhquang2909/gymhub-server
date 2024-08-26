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
    private  InMemoryRepository inMemoryRepository;

    public  MemberResponseDTO memberToMemberResponseDTO(Member member) {

        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setId(member.getId());
        dto.setUserName(member.getUserName());
        dto.setEmail(member.getEmail());
        dto.setTitle(member.getTitle());
        dto.setBio(member.getBio());
        dto.setStringAvatar(Base64.getEncoder().encodeToString(member.getAvatar()));
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


    public  MemberRequestDTO memberToMemberRequestDTO(Member member) {
        MemberRequestDTO dto = new MemberRequestDTO();

        dto.setUserName(member.getUserName());
        dto.setEmail(member.getEmail());
        dto.setPassword(member.getPassword());
        dto.setBio(member.getBio());
        dto.setStringAvatar(Base64.getEncoder().encodeToString(member.getAvatar()));
        return dto;
    }

    /**
    public  Member memberRequestToMember(MemberRequestDTO memberRequestDTO, String encodedPassword) {
        byte[] avatar = Base64.getDecoder().decode(memberRequestDTO.getStringAvatar());
        return new Member(
                memberRequestDTO.getUserName(),
                encodedPassword, // Encode the password before setting it
                memberRequestDTO.getEmail(),
                (java.sql.Date) new Date(System.currentTimeMillis())
        );
    }
     **/
    /**
     * Maps a Member entity and related ban information to a BannedMemberDTO.
     *
     * @param member The Member entity to be mapped.
     * @param bannedUntil The date until the member is banned.
     * @param reason The reason for the ban.
     * @return The BannedMemberDTO containing member and ban details.
     */

    public  BannedMemberDTO memberToBannedMemberDTO(Member member, Long bannedUntil, String reason) {
        Date banUntilDate = new Date(bannedUntil);
        return new BannedMemberDTO(
                member.getId(),
                member.getUserName(),
                banUntilDate,
                reason
        );
    }
}
