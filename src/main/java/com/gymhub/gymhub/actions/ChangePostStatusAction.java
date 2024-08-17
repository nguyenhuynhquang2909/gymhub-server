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
    private ToxicStatusEnum toxicStatus;
    private boolean resolveStatus;
    private String reason;


    public ChangePostStatusAction(Long actionId, String actionType, long postId, long threadId, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        super(actionId, actionType);
        this.postId = postId;
        this.threadId = threadId;
        this.toxicStatus = toxicStatus;
        this.resolveStatus = resolveStatus;
        this.reason = reason;
    }
}