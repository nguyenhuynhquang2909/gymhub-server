package com.gymhub.gymhub.actions;

public class ChangeThreadStatusAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private String category;
    private int from;
    private int to;
    private String reason;
    public ChangeThreadStatusAction(Long actionId, long threadId, String category, int from,
            int to, String reason) {
        super(actionId, "ChangeThreadStatus");
        this.threadId = threadId;
        this.category = category;
        this.from = from;
        this.to = to;
        this.reason = reason;
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
