package com.gymhub.gymhub.actions;

public class AddUserAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private Long userId;

    public AddUserAction(long userId) {
        super();
        this.actionType = "Add Users";
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
    

}
