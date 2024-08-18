package com.gymhub.gymhub.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingPostDTO {

    private Long postID;
    private String authorUsername;
    private String content;

    public PendingPostDTO(Long postID, String authorUsername, String content) {
        this.postID = postID;
        this.authorUsername = authorUsername;
        this.content = content;
    }
}
