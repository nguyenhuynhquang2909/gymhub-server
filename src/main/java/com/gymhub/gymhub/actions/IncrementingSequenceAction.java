package com.gymhub.gymhub.actions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class IncrementingSequenceAction extends MustLogAction{
    private SequenceType type;

    public IncrementingSequenceAction(SequenceType type) {
        super();
        this.type = type;
    }
}
