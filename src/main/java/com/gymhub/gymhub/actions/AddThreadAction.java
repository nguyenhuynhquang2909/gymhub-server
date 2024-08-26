package com.gymhub.gymhub.actions;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AddThreadAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private LocalDateTime creationDateTime;
    private ThreadCategoryEnum category;
    private ToxicStatusEnum toxicStatus;
    private long authorId;
    private boolean resolveStatus;
    private String reason;




    public AddThreadAction(long threadId, ThreadCategoryEnum category, LocalDateTime creationDateTime, ToxicStatusEnum toxicStatus, long authorId, boolean resolveStatus, String reason) {
        super();
        this.actionType = "Add Thread";
        this.threadId = threadId;
        this.category = category;
        this.creationDateTime = creationDateTime;
        this.toxicStatus = toxicStatus;
        this.authorId = authorId;
        this.resolveStatus = resolveStatus;
        this.reason = reason;

    }
}
