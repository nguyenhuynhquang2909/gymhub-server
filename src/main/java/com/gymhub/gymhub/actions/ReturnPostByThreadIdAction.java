package com.gymhub.gymhub.actions;

public class ReturnPostByThreadIdAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private int limit;
    private int offset;
    private long userId;
    public ReturnPostByThreadIdAction(Long actionId,  long threadId, int limit, int offset,
            long userId) {
        super(actionId, "ReturnPostByThreadId");
        this.threadId = threadId;
        this.limit = limit;
        this.offset = offset;
        this.userId = userId;
    }
    public long getThreadId() {
        return threadId;
    }
    public int getLimit() {
        return limit;
    }
    public int getOffset() {
        return offset;
    }
    public long getUserId() {
        return userId;
    }
    
    
}
