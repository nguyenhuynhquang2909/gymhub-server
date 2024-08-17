package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.in_memory.BanInfo;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.MemberMapper;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import com.gymhub.gymhub.repository.ModeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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


    public ResponseEntity<Void> updateModInfo(ModeratorRequestAndResponseDTO modDTO) {
        // Check if mod exists
        Optional<Moderator> mod = moderatorRepository.findModByUserName(modDTO.getUsername());
        if (mod.isPresent()) {
            Moderator existingMod = mod.get();
            if (!existingMod.getUserName().startsWith("mod")) {
                throw new IllegalArgumentException("Moderator username must start with 'mod'");
            }
            existingMod.setPassword(modDTO.getPassword());
            existingMod.setEmail(modDTO.getEmail());
            moderatorRepository.save(existingMod);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all pending posts
    public List<PostResponseDTO> getAllPendingPosts() {
        return (List<PostResponseDTO>) inMemoryRepository.getPendingPosts();
    }

    // Get all pending threads
    public List<ThreadResponseDTO> getAllPendingThreads() {
        return (List<ThreadResponseDTO>) inMemoryRepository.getPendingThreads();
    }


    /**
     * Bans a member by adding their ID to the bannedList with the time their ban will be lifted.
     *
     * @param userId The ID of the user to be banned.
     * @param banUntilDate The time in date when the ban will be lifted.
     */
    public void banMember(Long userId, Date banUntilDate, String banReason) {
        BanInfo banInfo = new BanInfo(banUntilDate, banReason);
        inMemoryRepository.saveBannedUser(userId, banInfo);
    }

    /**
     * Unbans a member by removing their ID from the bannedList.
     *
     * @param userId The ID of the user to be unbanned.
     */
    public void unbanMember(Long userId) {

        // Remove the banned user data from the InMemoryRepository
        inMemoryRepository.deleteBannedUser(userId);
    }

    /**
     * Checks if a member is banned.
     *
     * @param userId The ID of the user to check.
     * @return True if the user is banned, false otherwise.
     */
    public boolean isMemberBanned(Long userId) {
        return inMemoryRepository.isUserBanned(userId);
    }

    /**
     * Gets the ban expiration time for a member.
     *
     * @param userId The ID of the user.
     * @return The time in milliseconds when the ban will be lifted.
     */
    public Long getBanExpiration(Long userId) {
        return inMemoryRepository.findBanExpiration(userId);
    }

    //get all banned members

    /**
     * Retrieves a list of all banned members.
     *
     * @return A list of banned members.
     */
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

    /**
     * Displays all banned members as a list of BannedMemberDTOs.
     *
     * @param memberList A list of all members to avoid redundant database queries.
     * @return A list of BannedMemberDTO containing details of banned members.
     */
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
    //ban A Post
    public ResponseEntity<Void> banAPostWhileSurfing(ModeratorRequestAndResponseDTO modDTO, Long postId, Long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        // Check if mod exists
        Optional<Moderator> mod = moderatorRepository.findModByUserName(modDTO.getUsername());
        if (mod.isPresent()) {
            inMemoryRepository.changePostToxicStatusForModBanningWhileSurfingForum(postId, threadId,newToxicStatus, reason);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//ban A Thread => call cache method to change thread toxic status and resolve status
    public ResponseEntity<Void> banAThreadWhileSurfing(ModeratorRequestAndResponseDTO modDTO, Long threadId, String category, ToxicStatusEnum newToxicStatus, String reason) {
        // Check if mod exists
        Optional<Moderator> mod = moderatorRepository.findModByUserName(modDTO.getUsername());
        if (mod.isPresent()) {
          inMemoryRepository.changeThreadToxicStatusForModBanningWhileSurfingForum(threadId,category,newToxicStatus, reason );
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
