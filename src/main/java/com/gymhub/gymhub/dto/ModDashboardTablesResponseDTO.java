package com.gymhub.gymhub.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModDashboardTablesResponseDTO {
    private List<PostResponseDTO> pendingPosts;
    private List<ThreadResponseDTO> pendingThreads;
    private List<BannedMemberDTO> bannedMembers;

    public ModDashboardTablesResponseDTO(List<PostResponseDTO> pendingPosts, List<ThreadResponseDTO> pendingThreads, List<BannedMemberDTO> bannedMembers) {
        this.pendingPosts = pendingPosts;
        this.pendingThreads = pendingThreads;
        this.bannedMembers = bannedMembers;
    }
}
