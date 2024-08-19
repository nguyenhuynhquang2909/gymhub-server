package com.gymhub.gymhub.actions;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import lombok.Getter;

@Getter
public class AddThreadAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private ThreadCategoryEnum category;
    private ToxicStatusEnum toxicStatus;
    private long authorId;
    private boolean resolveStatus;
    private String reason;




    public AddThreadAction(Long actionId, String actionType, long threadId, ThreadCategoryEnum category, ToxicStatusEnum toxicStatus, long authorId, boolean resolveStatus, String reason) {
        super(actionId, "AddThread");
        this.threadId = threadId;
        this.category = category;
        this.toxicStatus = toxicStatus;
        this.authorId = authorId;
        this.resolveStatus = resolveStatus;
        this.reason = reason;

    }
}
