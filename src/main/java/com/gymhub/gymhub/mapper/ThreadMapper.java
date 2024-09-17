package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Tag;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.repository.InMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gymhub.gymhub.domain.Thread;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Component
public class ThreadMapper {

    @Autowired
    private InMemoryRepository inMemoryRepository;

    public ThreadResponseDTO toThreadResponseDTO(Thread thread) {
        ThreadResponseDTO dto = new ThreadResponseDTO();
        dto.setId(thread.getId());

        // Convert LocalDateTime to Long (epoch seconds)
        dto.setCreationDateTime(thread.getCreationDateTime());

        // Set ThreadResponseDTO fields from cache
        dto.setPostCount(inMemoryRepository.getPostCountOfAThreadByThreadId(thread.getId()));
        dto.setLikeCount(inMemoryRepository.getLikeCountByThreadId(thread.getId()));
        dto.setViewCount(inMemoryRepository.getThreadViewCountByThreadId(thread.getId()));
        dto.setReason(inMemoryRepository.getReasonByThreadId(thread.getId()));
        dto.setBeenLiked(inMemoryRepository.checkIfAThreadHasBeenLikedByAMemberId(thread.getId(), thread.getOwner().getId()));
        dto.setResolveStatus(inMemoryRepository.getResolveStatusByThreadId(thread.getId()));

        Integer toxicStatusBoolean = inMemoryRepository.getToxicStatusByThreadId(thread.getId());
        if (toxicStatusBoolean == 1) {
            dto.setToxicStatus(ToxicStatusEnum.NOT_TOXIC);
        } else if (toxicStatusBoolean == 0) {
            dto.setToxicStatus(ToxicStatusEnum.PENDING);
        } else if (toxicStatusBoolean == -1) {
            dto.setToxicStatus(ToxicStatusEnum.TOXIC);
        }


        dto.setAuthorName(thread.getOwner().getUserName());
        dto.setAuthorId(thread.getOwner().getId());
        dto.setAuthorAvatar(thread.getOwner().getStringAvatar());
        dto.setTitle(thread.getTitle());

        // Fetch and set tag IDs for the thread
        Set<Tag> tags = thread.getTags();
        int[] tagIdsArray = tags.stream()
                .mapToInt(tag -> tag.getId().intValue())  // Convert Set<Tag> to int[]
                .toArray();
        dto.setTagIds(tagIdsArray);

        dto.setThreadCategoryEnum(thread.getCategory());

        return dto;
    }


    public Thread toThread(ThreadRequestDTO threadRequestDTO) {
        Thread thread = new Thread();
        thread.setTitle(threadRequestDTO.getTitle());
        thread.setCategory(threadRequestDTO.getCategory());
        return thread;
    }

    public PendingThreadDTO threadToPendingThreadDTO(Thread thread) {
        if (thread == null) {
            return null;
        }

        Long ownerId = thread.getOwner().getId();
        Long threadId = thread.getId();
        ThreadCategoryEnum threadCategory = thread.getCategory();
        String authorUsername = thread.getOwner() != null ? thread.getOwner().getUserName() : null;
        String title = thread.getTitle();
        String reason = inMemoryRepository.getReasonByThreadId(threadId);

        return new PendingThreadDTO(ownerId,threadId, threadCategory, authorUsername, title, reason);
    }
}
