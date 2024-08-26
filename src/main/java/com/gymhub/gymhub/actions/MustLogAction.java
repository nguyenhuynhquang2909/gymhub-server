package com.gymhub.gymhub.actions;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

// TODO Create sub classes that extend this class corresponding to actions that have to be logged
@Getter
public abstract class MustLogAction implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String actionType;
    protected long timestamp;
    public MustLogAction() {
        this.timestamp = System.currentTimeMillis();
    }

    
}
