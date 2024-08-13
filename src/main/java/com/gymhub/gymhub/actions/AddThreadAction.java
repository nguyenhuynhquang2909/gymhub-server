package com.gymhub.gymhub.actions;
import lombok.Getter;

@Getter
public class AddThreadAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private String category;
    private String toxicStatus;
    private boolean resolveStatus;
    private long userId;
    public AddThreadAction(Long actionId, long threadId, String category, String toxicStatus, long userId, boolean resolveStatus) {
        super(actionId, "AddThread");
        this.threadId = threadId;
        this.category = category;
        this.toxicStatus = toxicStatus;
        this.userId = userId;
        this.resolveStatus = resolveStatus;
    }


}
