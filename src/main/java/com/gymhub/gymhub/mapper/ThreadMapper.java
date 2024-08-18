package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.InMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class ThreadMapper {

    @Autowired
    private InMemoryRepository inMemoryRepository;



    public ThreadResponseDTO toThreadResponseDTO(Thread thread, Long memberId) {
        ThreadResponseDTO dto = new ThreadResponseDTO();
        dto.setId(thread.getId());

        // Convert LocalDateTime to Long (epoch seconds)
        dto.setCreationDateTime(thread.getCreationDateTime().toEpochSecond(ZoneOffset.UTC));
        // Set ThreadResponseDTO fields from cache
        dto.setPostCount(inMemoryRepository. getPostCountOfAThreadByThreadId(thread.getId()));
        dto.setLikeCount(inMemoryRepository.getLikeCountByThreadId(thread.getId()));
        dto.setViewCount(inMemoryRepository.getThreadViewCountByThreadId(thread.getId()));
        dto.setReason(inMemoryRepository.getReasonByThreadId(thread.getId()));
        dto.setBeenLiked(inMemoryRepository.checkIfAThreadHasBeenLikedByAMemberId(thread.getId(), memberId));
        dto.setResolveStatus(inMemoryRepository.getResolveStatusByThreadId(thread.getId()));
        dto.setToxicStatus(inMemoryRepository.getToxicStatusByThreadId(thread.getId()));

        dto.setAuthorName(thread.getOwner().getUserName());
        dto.setAuthorId(thread.getOwner().getId());
        dto.setAuthorAvatar(thread.getOwner().getStringAvatar());
        dto.setTitle(thread.getTitle());



        return dto;
    }


    public Thread toThread(ThreadRequestDTO threadRequestDTO) {
        Thread thread = new Thread();
        thread.setTitle(threadRequestDTO.getTitle());
        thread.setCategory(threadRequestDTO.getCategory().toString());
        // Assuming Thread has a method to set owner ID directly, otherwise adjust accordingly
        // thread.setOwnerId(threadRequestDTO.getAuthorId());
        return thread;
    }

    public ThreadRequestDTO toThreadRequestDTO(Thread thread) {
        ThreadRequestDTO dto = new ThreadRequestDTO();
        dto.setTitle(thread.getTitle());
        dto.setCategory(ThreadCategoryEnum.valueOf(thread.getCategory().toUpperCase()));
        return dto;
    }
    public static PendingThreadDTO threadToPendingThreadDTO(Thread thread) {
        if (thread == null) {
            return null;
        }

        // Extract the required fields from the Thread entity
        Long threadId = thread.getId(); // Assuming getId() returns the ID of the Thread
        ThreadCategoryEnum threadCategory = ThreadCategoryEnum.valueOf(thread.getCategory()); // Assuming category is stored as a String and matches the enum values
        String authorUsername = thread.getOwner() != null ? thread.getOwner().getUserName() : null;
        String title = thread.getTitle();

        // Create and return a new PendingThreadDTO using the extracted values
        return new PendingThreadDTO(threadId, threadCategory, authorUsername, title);
    }

}
