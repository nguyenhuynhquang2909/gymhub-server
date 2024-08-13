package com.gymhub.gymhub.actions;

public class AddPostAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private long postId;
    private long userId;
    private int status;
    public AddPostAction(Long actionId, long threadId, long postId, long userId, String status) {
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
    public String getStatus() {
        return status;
    }
    

    
}
