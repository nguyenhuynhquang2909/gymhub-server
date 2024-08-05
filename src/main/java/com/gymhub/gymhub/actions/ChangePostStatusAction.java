package com.gymhub.gymhub.actions;

public class ChangePostStatusAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long postId;
    private long threadId;
    private String category;
    private int from;
    private int to;
    public ChangePostStatusAction(Long actionId, long postId, long threadId, String category, int from,
            int to) {
        super(actionId, "ChangePostStatus");
        this.postId = postId;
        this.threadId = threadId;
        this.category = category;
        this.from = from;
        this.to = to;
    }
    public long getPostId() {
        return postId;
    }
    public long getThreadId() {
        return threadId;
    }
    public String getCategory() {
        return category;
    }
    public int getFrom() {
        return from;
    }
    public int getTo() {
        return to;
    }
    
}
