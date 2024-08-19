package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.dto.PendingThreadDTO;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.dto.ThreadResponseDTO;
import com.gymhub.gymhub.repository.InMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gymhub.gymhub.domain.Thread;

import java.time.ZoneOffset;

@Component
public class ThreadMapper {

    @Autowired
    private InMemoryRepository inMemoryRepository;

    public ThreadResponseDTO toThreadResponseDTO(Thread thread) {
        ThreadResponseDTO dto = new ThreadResponseDTO();
        dto.setId(thread.getId());

        // Convert LocalDateTime to Long (epoch seconds)
        dto.setCreationDateTime(thread.getCreationDateTime().toEpochSecond(ZoneOffset.UTC));
        // Set ThreadResponseDTO fields from cache
        dto.setPostCount(inMemoryRepository.getPostCountOfAThreadByThreadId(thread.getId()));
        dto.setLikeCount(inMemoryRepository.getLikeCountByThreadId(thread.getId()));
        dto.setViewCount(inMemoryRepository.getThreadViewCountByThreadId(thread.getId()));
        dto.setReason(inMemoryRepository.getReasonByThreadId(thread.getId()));
        dto.setBeenLiked(inMemoryRepository.checkIfAThreadHasBeenLikedByAMemberId(thread.getId(), thread.getOwner().getId()));
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
        thread.setCategory(threadRequestDTO.getCategory());
        return thread;
    }

    public ThreadRequestDTO toThreadRequestDTO(Thread thread) {
        ThreadRequestDTO dto = new ThreadRequestDTO();
        dto.setTitle(thread.getTitle());
        dto.setCategory(thread.getCategory());
        return dto;
    }

    public PendingThreadDTO threadToPendingThreadDTO(Thread thread) {
        if (thread == null) {
            return null;
        }

        Long threadId = thread.getId();
        ThreadCategoryEnum threadCategory = thread.getCategory();
        String authorUsername = thread.getOwner() != null ? thread.getOwner().getUserName() : null;
        String title = thread.getTitle();
        String reason = inMemoryRepository.getReasonByThreadId(threadId);

        return new PendingThreadDTO(threadId, threadCategory, authorUsername, title, reason);
    }
}
