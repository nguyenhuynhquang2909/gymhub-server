package com.gymhub.gymhub.actions;

public class ViewThreadAction extends MustLogAction {
    private long threadId;

    public ViewThreadAction(Long actionId, long threadId) {
        super(actionId, "ViewThread");
        this.threadId = threadId;
    }

    public long getThreadId() {
        return threadId;
    }
    
    
}
