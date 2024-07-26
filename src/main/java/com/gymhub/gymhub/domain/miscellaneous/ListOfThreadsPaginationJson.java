package com.gymhub.gymhub.domain.miscellaneous;

import com.gymhub.gymhub.domain.Thread;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ListOfThreadsPaginationJson {
    private List<Thread> threads;
    private long after;

    public ListOfThreadsPaginationJson(List<Thread> threads, long after) {
        this.threads = threads;
        this.after = after;
    }
}
