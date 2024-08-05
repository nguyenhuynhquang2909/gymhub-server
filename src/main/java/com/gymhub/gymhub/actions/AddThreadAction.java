package com.gymhub.gymhub.actions;

public class AddThreadAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private String category;
    private int status;
    private long userId;
    public AddThreadAction(Long actionId, long threadId, String category, int status, long userId) {
        super(actionId, "AddThread");
        this.threadId = threadId;
        this.category = category;
        this.status = status;
        this.userId = userId;
    }
    public long getThreadId() {
        return threadId;
    }
    public String getCategory() {
        return category;
    }
    public int getStatus() {
        return status;
    }
    public long getUserId() {
        return userId;
    }
    


    

}
