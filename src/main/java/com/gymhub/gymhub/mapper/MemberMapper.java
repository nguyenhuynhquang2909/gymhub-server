package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.MemberResponseDTO;
import com.gymhub.gymhub.in_memory.Cache;

import java.util.Base64;
import java.util.Date;

public class MemberMapper {

    public static MemberResponseDTO toMemberResponseDTO(Member member, Cache cache) {
        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setId(member.getId());
        dto.setUserName(member.getUserName());
        dto.setEmail(member.getEmail());
        dto.setTitle(member.getTitle());
        dto.setBio(member.getBio());
        dto.setStringAvatar(Base64.getEncoder().encodeToString(member.getAvatar()));
        dto.setJoinDate(member.getJoinDate());
//        dto.setLastSeen(member. getLastSeen());
        dto.setLikeCount(cache.getMemberTotalLikeCountByMemberId(member.getId()));
        dto.setPostCount(cache.getMemberTotalPostCountByMemberId(member.getId()));
//        dto.setFollowerCount(cache.getFollowerCountByMemberId(member.getId()));
        return dto;
    }

    public static Member toMember(MemberRequestDTO memberRequestDTO, String encodedPassword) {
        byte[] avatar = Base64.getDecoder().decode(memberRequestDTO.getStringAvatar());
        return new Member(
                memberRequestDTO.getId(),
                memberRequestDTO.getUserName(),
                encodedPassword, // Encode the password before setting it
                memberRequestDTO.getEmail(),
                (java.sql.Date) new Date(System.currentTimeMillis())
        );
    }
}
