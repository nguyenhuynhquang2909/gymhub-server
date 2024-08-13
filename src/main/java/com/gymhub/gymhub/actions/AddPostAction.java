package com.gymhub.gymhub.actions;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPostAction extends MustLogAction {
    private static final long serialVersionUID = 1L;
    private long threadId;
    private long postId;
    private long userId;
    private String toxicStatus;
//    @Getter(value = AccessLevel.PUBLIC, onMethod_ = @__({@Override}))
    public boolean resolveStatus;
    public AddPostAction(Long actionId, long threadId, long postId, long userId, String toxicStatus, boolean resolveStatus) {
        super(actionId, "AddPost");
        this.threadId = threadId;
        this.postId = postId;
        this.userId = userId;
        this.toxicStatus = toxicStatus;
        this.resolveStatus = resolveStatus;
    }

    

    
}
