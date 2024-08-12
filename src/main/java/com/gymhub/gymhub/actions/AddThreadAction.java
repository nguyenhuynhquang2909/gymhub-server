package com.gymhub.gymhub.actions;

public class AddThreadAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private String category;
    private String status;
    private long userId;
    public AddThreadAction(Long actionId, long threadId, String category, String status, long userId) {
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
    public String getStatus() {
        return status;
    }
    public long getUserId() {
        return userId;
    }
    


    

}
