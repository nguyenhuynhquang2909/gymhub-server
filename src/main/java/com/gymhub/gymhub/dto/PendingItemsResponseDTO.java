package com.gymhub.gymhub.dto;

import java.util.List;

public class PendingItemsResponseDTO {
    private List<PostResponseDTO> pendingPosts;
    private List<ThreadResponseDTO> pendingThreads;

    public PendingItemsResponseDTO(List<PostResponseDTO> pendingPosts, List<ThreadResponseDTO> pendingThreads) {
        this.pendingPosts = pendingPosts;
        this.pendingThreads = pendingThreads;
    }

    // Getters and setters
    public List<PostResponseDTO> getPendingPosts() {
        return pendingPosts;
    }

    public void setPendingPosts(List<PostResponseDTO> pendingPosts) {
        this.pendingPosts = pendingPosts;
    }

    public List<ThreadResponseDTO> getPendingThreads() {
        return pendingThreads;
    }

    public void setPendingThreads(List<ThreadResponseDTO> pendingThreads) {
        this.pendingThreads = pendingThreads;
    }
}
