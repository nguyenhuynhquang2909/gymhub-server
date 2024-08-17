package com.gymhub.gymhub.actions;

    import com.gymhub.gymhub.dto.ToxicStatusEnum;
    import lombok.Setter;
    import lombok.Getter;

    @Setter
    @Getter
    public class ChangeThreadStatusAction extends MustLogAction {
        private static final long serialVersionUID = 1L;
        private long threadId;
        private String category;
        private ToxicStatusEnum toxicStatus;
        private boolean resolveStatus;
        private String reason;



        public ChangeThreadStatusAction(Long actionId, String actionType, long threadId, String category, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
            super(actionId, actionType);
            this.threadId = threadId;
            this.category = category;
            this.toxicStatus = toxicStatus;
            this.resolveStatus = resolveStatus;
            this.reason = reason;
        }
    }