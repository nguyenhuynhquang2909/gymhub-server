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

import java.util.*;
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

    @Autowired
    private ThreadMapper threadMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private ModeratorMapper moderatorMapper;
    @Autowired
    private MemberMapper memberMapper;

    public void updateModInfo(ModeratorRequestAndResponseDTO modDTO) {
        Moderator existingMod = moderatorMapper.modDTOToMod(modDTO);
        if (!existingMod.getUserName().startsWith("mod")) {
            throw new IllegalArgumentException("Moderator username must start with 'mod'");
        }
        existingMod.setPassword(modDTO.getPassword());
        existingMod.setEmail(modDTO.getEmail());
        moderatorRepository.save(existingMod);
    }

    public List<PendingPostDTO> getAllPendingPosts() {
        // Get the pending posts map from the inMemoryRepository
        HashMap<Long, HashMap<Integer, LinkedList<Long>>> pendingPostsMap = inMemoryRepository.getPendingPosts();
        List<PendingPostDTO> pendingPostDTOs = new ArrayList<>();

        // Use a set to avoid duplicates
        Set<Long> uniquePostIds = new HashSet<>();

        // Iterate over the map entries and filter posts with status PENDING (0)
        for (Map.Entry<Long, HashMap<Integer, LinkedList<Long>>> threadEntry : pendingPostsMap.entrySet()) {
            Long threadId = threadEntry.getKey();
            HashMap<Integer, LinkedList<Long>> postsByStatus = threadEntry.getValue();

            // Get the list of pending posts (status 0 is for pending posts)
            LinkedList<Long> pendingPostsInThread = postsByStatus.get(0); // Assuming 0 is the status for pending posts

            if (pendingPostsInThread != null) {
                for (Long postId : pendingPostsInThread) {
                    // Check if the post ID is already processed
                    if (!uniquePostIds.contains(postId)) {
                        Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new RuntimeException("Post not found"));

                        PendingPostDTO pendingPostDTO = postMapper.postToPendingPostDTO(post);
                        pendingPostDTOs.add(pendingPostDTO);

                        // Add post ID to the set to avoid duplicates
                        uniquePostIds.add(postId);
                    }
                }
            }

        }

        return pendingPostDTOs;
    }



    public List<PendingThreadDTO> getAllPendingThreads() {
        // Get the pending threads map from the inMemoryRepository
        HashMap<ThreadCategoryEnum, HashMap<Integer, LinkedList<Long>>> pendingThreadsMap = inMemoryRepository.getPendingThreads();
        List<PendingThreadDTO> pendingThreadDTOs = new ArrayList<>();

        // Use a set to avoid duplicates
        Set<Long> uniqueThreadIds = new HashSet<>();

        // Iterate over the map entries and filter threads with status PENDING (0)
        for (Map.Entry<ThreadCategoryEnum, HashMap<Integer, LinkedList<Long>>> categoryEntry : pendingThreadsMap.entrySet()) {
            HashMap<Integer, LinkedList<Long>> threadsByStatus = categoryEntry.getValue();

            // Get the list of pending threads (status 0 is for pending threads)
            LinkedList<Long> pendingThreadsInCategory = threadsByStatus.get(0);

            if (pendingThreadsInCategory != null) {
                for (Long threadId : pendingThreadsInCategory) {
                    // Check if the thread ID is already processed
                    if (!uniqueThreadIds.contains(threadId)) {
                        Thread thread = threadRepository.findById(threadId)
                                .orElseThrow(() -> new RuntimeException("Thread not found"));

                        // Ensure the thread has toxic status PENDING
                        if (threadMapper.toThreadResponseDTO(thread).getToxicStatus() == ToxicStatusEnum.PENDING) {
                            PendingThreadDTO pendingThreadDTO = threadMapper.threadToPendingThreadDTO(thread);
                            pendingThreadDTOs.add(pendingThreadDTO);
                            // Add thread ID to the set to avoid duplicates
                            uniqueThreadIds.add(threadId);
                        }
                    }
                }
            }
        }

        return pendingThreadDTOs;
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
                BannedMemberDTO bannedMemberDTO = memberMapper.memberToBannedMemberDTO(
                        member,
                        banInfo.getBanUntilDate().getTime(),
                        banInfo.getBanReason()
                );
                bannedMemberDTOs.add(bannedMemberDTO);
            }
        }
        System.out.println("banned Members: " + bannedMemberDTOs);
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
