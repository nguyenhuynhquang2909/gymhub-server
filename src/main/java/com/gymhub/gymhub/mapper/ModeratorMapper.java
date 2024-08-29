package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.ModeratorRequestAndResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ModeratorMapper {

    public  ModeratorRequestAndResponseDTO modToModDTO(Moderator moderator) {
        ModeratorRequestAndResponseDTO dto = new ModeratorRequestAndResponseDTO();
        dto.setUsername(moderator.getUserName());
        dto.setEmail(moderator.getEmail());
        dto.setPassword(moderator.getPassword()); // For now, include the password
        return dto;
    }

    public  Moderator modDTOToMod(ModeratorRequestAndResponseDTO moderatorRequestAndResponseDTO) {
        return new Moderator(
                moderatorRequestAndResponseDTO.getId(),
                moderatorRequestAndResponseDTO.getUsername(),
                moderatorRequestAndResponseDTO.getPassword(), // Password should be handled securely later on
                moderatorRequestAndResponseDTO.getEmail()
        );
    }

    public ModeratorRequestAndResponseDTO customUserDetailToDTO(CustomUserDetails userDetails) {
        ModeratorRequestAndResponseDTO dto = new ModeratorRequestAndResponseDTO();
        dto.setId(userDetails.getId());
        dto.setUsername(userDetails.getUsername());
        dto.setPassword(userDetails.getPassword());
        return dto;
    }
}
