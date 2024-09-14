package com.gymhub.gymhub.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingPostDTO {
    private Long postID;
    private String authorUsername;
    private String content;
    private String reason; //Reasons for the suspicion of toxicity (from AI detect or from user report)
    //Add 1 more field:
    private Long threadId;

    public PendingPostDTO(Long postID, String authorUsername, String content, String reason, Long threadId) {
        this.postID = postID;
        this.authorUsername = authorUsername;
        this.content = content;
        this.reason = reason;
        this.threadId = threadId;
    }
}
