package com.gymhub.gymhub.actions;

public class ViewThreadAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;

    public ViewThreadAction(Long actionId, long threadId) {
        super(actionId, "ViewThread");
        this.threadId = threadId;
    }

    public long getThreadId() {
        return threadId;
    }
    
    
}
