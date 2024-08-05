package com.gymhub.gymhub.actions;

public class LikePostAction extends MustLogAction {
    private long postId;
    private long userId;
    private int mode;
    public LikePostAction(Long actionId, String actionType, long postId, long userId, int mode) {
        super(actionId, "LikePost");
        this.postId = postId;
        this.userId = userId;
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
}
