package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Tag;
import com.gymhub.gymhub.dto.TagResponseDTO;

public class TagMapper {

    public static TagResponseDTO convertToDResponseTO(Tag tag) {
        TagResponseDTO tagDTO = new TagResponseDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setTagName(tag.getTagName());
        return tagDTO;
    }
}
