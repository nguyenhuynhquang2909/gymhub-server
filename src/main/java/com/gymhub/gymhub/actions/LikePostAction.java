package com.gymhub.gymhub.actions;

public class LikePostAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long postId;
    private long userId;
    private long threadId;
    private int mode;
    public LikePostAction(long postId, long userId, long threadId, int mode) {
        super();
        this.actionType = "Like Post";
        this.postId = postId;
        this.userId = userId;
        this.threadId = threadId;
        this.mode = mode;
    }
    public long getPostId() {
        return postId;
    }
    public long getUserId() {
        return userId;
    }
    public int getMode() {
        return mode;
    }

    public long getThreadId() {
        return threadId;
    }
}
