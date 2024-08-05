package com.gymhub.gymhub.actions;

public class LikeThreadAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private long userId;
    private int mode;
    public LikeThreadAction(Long actionId, String actionType, long threadId, long userId, int mode) {
        super(actionId, "LikeThread");
        this.threadId = threadId;
        this.userId = userId;
        this.mode = mode;
    }
    public long getThreadId() {
        return threadId;
    }
    public long getUserId() {
        return userId;
    }
    public int getMode() {
        return mode;
    }
    
    

}
