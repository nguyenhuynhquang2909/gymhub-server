package com.gymhub.gymhub.actions;

public class ReturnThreadByCategoryAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private int limit;
    private int offset;
    private Long userId;
    public ReturnThreadByCategoryAction(Long actionId, String category, long threadId, int limit, int offset,
            Long userId) {
        super(actionId, "ReturnThreadByCategory");
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
    public Long getUserId() {
        return userId;
    }
    


    
}
