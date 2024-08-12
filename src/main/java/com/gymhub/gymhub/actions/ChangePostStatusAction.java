package com.gymhub.gymhub.actions;

public class ChangePostStatusAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long postId;
    private long threadId;
    private String category;
    private int from;
    private int to;
    private String reason;
    public ChangePostStatusAction(Long actionId, long postId, long threadId, String category, int from,
                                  int to, String reason) {
        super(actionId, "ChangePostStatus");
        this.postId = postId;
        this.threadId = threadId;
        this.category = category;
        this.from = from;
        this.to = to;
        this.reason = reason;
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
    public String getReason() {
        return reason;
    }

}