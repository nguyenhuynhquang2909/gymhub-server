package com.gymhub.gymhub.actions;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddPostAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private long postId;
    private long userId;
    private String toxicStatus;
    private boolean resolveStatus;
    private String reason;

    public AddPostAction(long threadId, long postId, long userId, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        super();
        this.actionType = "Add Post";
        this.threadId = threadId;
        this.postId = postId;
        this.userId = userId;
        this.toxicStatus = toxicStatus.name();
        this.resolveStatus = resolveStatus;
        this.reason = reason;
    }

    

    
}
