package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.ModeratorRequestAndResponseDTO;

public class ModeratorMapper {

    public static ModeratorRequestAndResponseDTO toModeratorDTO(Moderator moderator) {
        ModeratorRequestAndResponseDTO dto = new ModeratorRequestAndResponseDTO();
        dto.setUsername(moderator.getUserName());
        dto.setEmail(moderator.getEmail());
        dto.setPassword(moderator.getPassword()); // For now, include the password
        return dto;
    }

    public static Moderator toModerator(ModeratorRequestAndResponseDTO moderatorRequestAndResponseDTO) {
        return new Moderator(
                moderatorRequestAndResponseDTO.getId(),
                moderatorRequestAndResponseDTO.getUsername(),
                moderatorRequestAndResponseDTO.getPassword(), // Password should be handled securely later on
                moderatorRequestAndResponseDTO.getEmail()
        );
    }
}
