package com.gymhub.gymhub.actions;

import com.gymhub.gymhub.dto.ToxicStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePostStatusAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long postId;
    private long threadId;
    private String toxicStatus;
    private boolean resolveStatus;
    private String reason;


    public ChangePostStatusAction(long postId, long threadId, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        super();
        this.actionType = "Change Post Status";
        this.postId = postId;
        this.threadId = threadId;
        this.toxicStatus = toxicStatus.name();
        this.resolveStatus = resolveStatus;
        this.reason = reason;
    }
}