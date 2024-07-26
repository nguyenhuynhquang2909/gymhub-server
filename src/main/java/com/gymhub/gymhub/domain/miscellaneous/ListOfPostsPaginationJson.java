package com.gymhub.gymhub.domain.miscellaneous;

import com.gymhub.gymhub.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ListOfPostsPaginationJson {
    private List<Post> posts;
    private long after;

    public ListOfPostsPaginationJson(List<Post> posts, long after) {
        this.posts = posts;
        this.after = after;
    }
}
