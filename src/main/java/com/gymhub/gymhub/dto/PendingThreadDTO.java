package com.gymhub.gymhub.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingThreadDTO {

    private Long threadId;
    private ThreadCategoryEnum threadCategory;
    private String authorUsername;
    private String title;
    private String reason;

    public PendingThreadDTO(Long threadId, ThreadCategoryEnum threadCategory, String authorUsername, String title, String reason) {
        this.threadId = threadId;
        this.threadCategory = threadCategory;
        this.authorUsername = authorUsername;
        this.title = title;
        this.reason = reason;
    }
}
