package com.gymhub.gymhub.actions;

    import com.gymhub.gymhub.dto.ThreadCategoryEnum;
    import com.gymhub.gymhub.dto.ToxicStatusEnum;
    import lombok.Setter;
    import lombok.Getter;

    @Setter
    @Getter
    public class ChangeThreadStatusAction extends MustLogAction {
        private static final long serialVersionUID = 1L;
        private long threadId;
        private ThreadCategoryEnum category;
        private ToxicStatusEnum toxicStatus;
        private boolean resolveStatus;
        private String reason;



        public ChangeThreadStatusAction(long threadId, ThreadCategoryEnum category, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
            super();
            this.actionType = "Change Thread Status";
            this.threadId = threadId;
            this.category = category;
            this.toxicStatus = toxicStatus;
            this.resolveStatus = resolveStatus;
            this.reason = reason;
        }
    }