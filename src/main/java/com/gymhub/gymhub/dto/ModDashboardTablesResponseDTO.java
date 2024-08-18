package com.gymhub.gymhub.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModDashboardTablesResponseDTO {
    private List<PendingPostDTO> pendingPosts;
    private List<PendingThreadDTO> pendingThreads;
    private List<BannedMemberDTO> bannedMembers;

    public ModDashboardTablesResponseDTO(List<PendingPostDTO> pendingPosts, List<PendingThreadDTO> pendingThreads, List<BannedMemberDTO> bannedMembers) {
        this.pendingPosts = pendingPosts;
        this.pendingThreads = pendingThreads;
        this.bannedMembers = bannedMembers;
    }
}
