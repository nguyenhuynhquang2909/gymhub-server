package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.in_memory.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class ThreadMapper {

    @Autowired
    private Cache cache;



    public ThreadResponseDTO toThreadResponseDTO(Thread thread, Long memberId) {
        ThreadResponseDTO dto = new ThreadResponseDTO();
        dto.setId(thread.getId());

        // Convert LocalDateTime to Long (epoch seconds)
        dto.setCreationDateTime(thread.getCreationDateTime().toEpochSecond(ZoneOffset.UTC));
        // Set ThreadResponseDTO fields from cache
        dto.setPostCount(cache.getPostCountOfAThreadByThreadId(thread.getId()));
        dto.setLikeCount(cache.getLikeCountByThreadId(thread.getId()));
        dto.setViewCount(cache.getThreadViewCountByThreadId(thread.getId()));
        dto.setReason(cache.getReasonByThreadId(thread.getId()));
        dto.setBeenLiked(cache.checkIfAThreadHasBeenLikedByAMemberId(thread.getId(), memberId));
        dto.setResolveStatus(cache.getResolveStatusByThreadId(thread.getId()));
        dto.setToxicStatus(cache.getToxicStatusByThreadId(thread.getId()));

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
        dto.setAuthorId(thread.getOwner().getId());
        dto.setCategory(ThreadCategoryEnum.valueOf(thread.getCategory().toUpperCase()));
        return dto;
    }
    public ThreadToxicFlowDTO toThreadToxicFlowDTO(Thread thread) {
        ThreadToxicFlowDTO dto = new ThreadToxicFlowDTO();
        dto.setId(thread.getId());
        dto.setThreadCategory(ThreadCategoryEnum.valueOf(thread.getCategory().toUpperCase()));
        return dto;
    }
}
