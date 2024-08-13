package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MemberService {
    @Autowired
    private Cache cache;

    @Autowired
    private MemberRepository memberRepository;

    public void followMember(Long followerId, Long followingId) {
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Following not found"));
        follower.follow(followingId);
        following.addFollower(followerId);

        cache.follow(followerId, followingId);
    }

    public void unfollowMember(Long followerId, Long followingId) {
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Following member not found"));
    }
    public Set<Long> getFollowers(Long memberId) {
        return cache.getFollowers(memberId);
    }
    public Set<Long> getFollowing(Long memberId) {
        return cache.getFollowing(memberId);
    }
    public int getFollowersNumber(Long memberId) {
        Set<Long> followers = cache.getFollowers(memberId);
        return followers != null ? followers.size(): 0;
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

    public void test() {

    }
}
