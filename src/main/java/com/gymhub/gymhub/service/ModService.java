package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.in_memory.BanInfo;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.MemberMapper;
import com.gymhub.gymhub.mapper.ModeratorMapper;
import com.gymhub.gymhub.mapper.PostMapper;
import com.gymhub.gymhub.mapper.ThreadMapper;
import com.gymhub.gymhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModService {

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Autowired
    private InMemoryRepository inMemoryRepository;

    @Autowired
    private Cache cache;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ThreadRepository threadRepository;

    public void updateModInfo(ModeratorRequestAndResponseDTO modDTO) {
        Moderator existingMod = ModeratorMapper.modDTOToMod(modDTO);
        if (!existingMod.getUserName().startsWith("mod")) {
            throw new IllegalArgumentException("Moderator username must start with 'mod'");
        }
        existingMod.setPassword(modDTO.getPassword());
        existingMod.setEmail(modDTO.getEmail());
        moderatorRepository.save(existingMod);
    }

    public List<PendingPostDTO> getAllPendingPosts() {
        List<PostResponseDTO> pendingPosts = (List<PostResponseDTO>) inMemoryRepository.getPendingPosts();

        // Convert the PostResponseDTO list to a list of PendingPostDTOs
        return pendingPosts.stream()
                .map(postResponseDTO -> {
                    Post post = postRepository.findById(postResponseDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Post not found"));
                    return PostMapper.postToPendingPostDTO(post);
                })
                .collect(Collectors.toList());
    }


    public List<PendingThreadDTO> getAllPendingThreads() {
        List<ThreadResponseDTO> pendingThreads = (List<ThreadResponseDTO>) inMemoryRepository.getPendingThreads();

        // Convert the ThreadResponseDTO list to a list of PendingThreadDTOs
        return pendingThreads.stream()
                .map(threadResponseDTO -> {
                    Thread thread = threadRepository.findById(threadResponseDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Thread not found"));
                    return ThreadMapper.threadToPendingThreadDTO(thread);
                })
                .collect(Collectors.toList());
    }


    public void banMember(Long userId, Date banUntilDate, String banReason) {
        BanInfo banInfo = new BanInfo(banUntilDate, banReason);
        inMemoryRepository.saveBannedUser(userId, banInfo);
    }

    public void unbanMember(Long userId) {
        inMemoryRepository.deleteBannedUser(userId);
    }

    public boolean isMemberBanned(Long userId) {
        return inMemoryRepository.isUserBanned(userId);
    }

    public Long getBanExpiration(Long userId) {
        return inMemoryRepository.findBanExpiration(userId);
    }

    public List<Member> getBannedMembers() {
        List<Member> bannedMembers = new ArrayList<>();
        for (Long userId : cache.getBannedList().keySet()) {
            Member member = memberRepository.findById(userId).orElse(null);
            if (member != null) {
                bannedMembers.add(member);
            }
        }
        return bannedMembers;
    }

    public List<BannedMemberDTO> displayBannedMembers(List<Member> memberList) {
        List<BannedMemberDTO> bannedMemberDTOs = new ArrayList<>();
        for (Long userId : cache.getBannedList().keySet()) {
            Member member = memberList.stream()
                    .filter(m -> m.getId().equals(userId))
                    .findFirst()
                    .orElse(null);
            if (member != null) {
                BanInfo banInfo = cache.getBannedList().get(userId);
                BannedMemberDTO bannedMemberDTO = MemberMapper.memberToBannedMemberDTO(
                        member,
                        banInfo.getBanUntilDate().getTime(),
                        banInfo.getBanReason()
                );
                bannedMemberDTOs.add(bannedMemberDTO);
            }
        }
        return bannedMemberDTOs;
    }

    public void banAPostWhileSurfing(Long postId, Long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        inMemoryRepository.changePostToxicStatusForModBanningWhileSurfingForum(postId, threadId, newToxicStatus, reason);
    }

    public void banAThreadWhileSurfing(Long threadId, ThreadCategoryEnum category, ToxicStatusEnum newToxicStatus, String reason) {
        inMemoryRepository.changeThreadToxicStatusForModBanningWhileSurfingForum(threadId, category, newToxicStatus, reason);
    }

    public void resolveAPendingThread(Long threadId, ThreadCategoryEnum category, ToxicStatusEnum newToxicStatus, String reason) {
        inMemoryRepository.changeThreadToxicStatusFromModDashBoard(threadId, category, newToxicStatus, reason);
    }

    public void resolveAPendingPost(Long postId, Long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        inMemoryRepository.changePostToxicStatusFromModDashboard(postId, threadId, newToxicStatus, reason);
    }
}
