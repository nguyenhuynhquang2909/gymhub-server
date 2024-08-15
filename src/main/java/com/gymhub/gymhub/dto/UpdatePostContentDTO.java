package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@Schema(description = "Contains the post's new content and images")
public class UpdatePostContentDTO {

    @Schema(description = "Post's  ID")
    private Long postId;
    @Schema(description = "Post's author ID")
    private Long authorId;
    @Schema(description = "Post's thread ID")
    private Long threadId;
    @Schema(description = "New Content")
    private String content;
    @Schema(description = "New images encoded as Strings")
    private List<String> encodedImage;



    //update both content and image


    public UpdatePostContentDTO(Long postId, Long authorId, Long threadId, String content, List<String> encodedImage) {
        this.postId = postId;
        this.authorId = authorId;
        this.threadId = threadId;
        this.content = content;
        this.encodedImage = encodedImage;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(List<String> encodedImage) {
        this.encodedImage = encodedImage;
    }
}
