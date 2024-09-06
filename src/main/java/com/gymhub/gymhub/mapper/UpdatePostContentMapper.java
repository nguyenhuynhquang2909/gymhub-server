package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.dto.UpdatePostContentDTO;
import org.springframework.stereotype.Component;

@Component
public class UpdatePostContentMapper {

    // Method to map UpdatePostContentDTO to an existing Post entity
    public  void updatePostFromDTO(UpdatePostContentDTO updatePostContentDTO, Post post) {
        // Update the content
        post.setContent(updatePostContentDTO.getContent());

        // Update the image if provided
        if (updatePostContentDTO.getEncodedImage() != null && !(updatePostContentDTO.getEncodedImage().length > 0)) {
            // Assuming only one image is allowed
            byte[] encodedImage = updatePostContentDTO.getEncodedImage();

            // If a post already has an image, update it, otherwise create a new one
            if (post.getImage() != null) {
                post.getImage().setEncodedImage(encodedImage);
            } else {
                Image newImage = new Image(encodedImage);
                newImage.setPost(post);
                post.setImage(newImage);
            }
        }
    }
}
