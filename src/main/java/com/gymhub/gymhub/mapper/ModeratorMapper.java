package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.ModeratorDTO;

public class ModeratorMapper {

    public static ModeratorDTO toModeratorDTO(Moderator moderator) {
        ModeratorDTO dto = new ModeratorDTO();
        dto.setUsername(moderator.getUserName());
        dto.setEmail(moderator.getEmail());
        dto.setPassword(moderator.getPassword()); // For now, include the password
        return dto;
    }

    public static Moderator toModerator(ModeratorDTO moderatorDTO) {
        return new Moderator(
                moderatorDTO.getId(),
                moderatorDTO.getUsername(),
                moderatorDTO.getPassword(), // Password should be handled securely later on
                moderatorDTO.getEmail()
        );
    }
}
