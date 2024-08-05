package com.gymhub.gymhub.actions;

public class AddPostAction extends MustLogAction {
    private long threadId;
    private long postId;
    private long userId;
    private int status;
    public AddPostAction(Long actionId, String actionType, long threadId, long postId, long userId, int status) {
        super(actionId, "AddPost");
        this.threadId = threadId;
        this.postId = postId;
        this.userId = userId;
        this.status = status;
    }
    public long getThreadId() {
        return threadId;
    }
    public long getPostId() {
        return postId;
    }
    public long getUserId() {
        return userId;
    }
    public int getStatus() {
        return status;
    }
    

    
}
