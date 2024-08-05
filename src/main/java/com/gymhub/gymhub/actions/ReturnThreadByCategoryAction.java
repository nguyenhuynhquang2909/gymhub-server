package com.gymhub.gymhub.actions;

public class ReturnThreadByCategoryAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private String category;
    private Long userId;
    private int limit;
    private int offset;
    public ReturnThreadByCategoryAction(Long actionId, String category, Long userId,int limit, int offset) {
        super(actionId, "ReturnThreadByCategory");
        this.category = category;
        this.limit = limit;
        this.offset = offset;
        this.userId = userId;
    }
    
    

    public String getCategory() {
        return category;
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
