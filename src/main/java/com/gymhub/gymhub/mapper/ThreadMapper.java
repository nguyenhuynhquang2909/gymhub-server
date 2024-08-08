package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.dto.ThreadResponseDTO;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.in_memory.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreadMapper {

    @Autowired
    private Cache cache;

    public ThreadResponseDTO toThreadResponseDTO(Thread thread, Long memberId ) {
        ThreadResponseDTO dto = new ThreadResponseDTO();
        dto.setId(thread.getId());
        dto.setCreationDateTime(thread.getCreationDateTime());
        dto.setLikeCount(cache.getLikeCountByThreadId(thread.getId()));
        dto.setViewCount(cache.getThreadViewCountByThreadId(thread.getId()));
        dto.setBeenReport(cache.checkIfAThreadHasBeenReportByThreadId(thread.getId()));
        dto.setPostCount(cache.getPostCountOfAThreadByThreadId(thread.getId()));
        dto.setAuthorName(thread.getOwner().getUserName());
        dto.setAuthorId(thread.getOwner().getId());
        dto.setAuthorAvatar(thread.getOwner().getStringAvatar());
        dto.setName(thread.getName());
        return dto;
    }

    public Thread toThread(ThreadRequestDTO threadRequestDTO) {
        Thread thread = new Thread();
        thread.setName(threadRequestDTO.getTitle());
        thread.setCategory(threadRequestDTO.getCategory().toString());
        // Assuming Thread has a method to set owner ID directly, otherwise adjust accordingly
        // thread.setOwnerId(threadRequestDTO.getAuthorId());
        return thread;
    }

    public ThreadRequestDTO toThreadRequestDTO(Thread thread) {
        ThreadRequestDTO dto = new ThreadRequestDTO();
        dto.setTitle(thread.getName());
        dto.setAuthorId(thread.getOwner().getId());
        dto.setCategory(ThreadCategoryEnum.valueOf(thread.getCategory().toUpperCase()));
        return dto;
    }
}
