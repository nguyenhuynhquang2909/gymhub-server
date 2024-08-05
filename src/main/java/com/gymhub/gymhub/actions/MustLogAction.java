package com.gymhub.gymhub.actions;

import java.io.Serializable;

// TODO Create sub classes that extend this class corresponding to actions that have to be logged
public abstract class MustLogAction implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Long actionId;
    protected String actionType;
    protected long timestamp;
    public MustLogAction(Long actionId, String actionType) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.timestamp = System.currentTimeMillis();
    }
    public Long getActionId() {
        return actionId;
    }
    public String getActionType() {
        return actionType;
    }
    public long getTimestamp() {
        return timestamp;
    }
    
}
