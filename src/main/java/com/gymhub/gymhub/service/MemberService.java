package com.gymhub.gymhub.service;

import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.MemberRepository;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
public class MemberService{

    @Autowired
    private Cache cache;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    public void followMember(Long followerId, Long followingId) {
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Following member not found"));
        follower.follow(followingId);
        following.addFollower(followerId);

        cache.follow(followerId, followingId);
    }

    public void unfollowMember(Long followerId, Long followingId) {
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Following member not found"));
        follower.unfollow(followingId);
        following.removeFollower(followerId);

        cache.unfollow(followerId, followingId);
    }

    public Set<Long> getFollowers(Long memberId) {
        return cache.getFollowers(memberId);
    }

    public Set<Long> getFollowing(Long memberId) {
        return cache.getFollowing(memberId);
    }

    public int getFollowersNumber(Long memberId) {
        Set<Long> followers = cache.getFollowers(memberId);
        return followers != null ? followers.size() : 0;
    }

    public int getFollowingNumber(Long memberId) {
        Set<Long> following = cache.getFollowing(memberId);
        return following != null ? following.size() : 0;
    }

    public Long getMemberIdFromUserName(String userName) {
        return memberRepository.findByUserName(userName)
                .map(Member::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ResponseEntity<Void> updateMemberInfo(MemberRequestDTO memberRequestDTO) {
        Optional<Member> member = userRepository.findById(memberRequestDTO.getId());
        if (member.isPresent()) {
            Member existingMember = member.get();
            existingMember.setPassword(memberRequestDTO.getPassword());
            existingMember.setEmail(memberRequestDTO.getEmail());
            existingMember.setAvatar(memberRequestDTO.getStringAvatar().getBytes());
            existingMember.setBio(memberRequestDTO.getBio());
            userRepository.save(existingMember);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
