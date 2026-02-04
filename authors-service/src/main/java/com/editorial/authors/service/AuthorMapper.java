package com.editorial.authors.service;

import com.editorial.authors.dto.AuthorDTO;
import com.editorial.authors.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorDTO entityToDTO(Author entity) {
        if (entity == null) {
            return null;
        }

        return AuthorDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .biography(entity.getBiography())
                .organization(entity.getOrganization())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Author dtoToEntity(AuthorDTO dto) {
        if (dto == null) {
            return null;
        }

        return Author.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .biography(dto.getBiography())
                .organization(dto.getOrganization())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public void updateEntityFromDTO(AuthorDTO dto, Author entity) {
        if (dto == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getBiography() != null) {
            entity.setBiography(dto.getBiography());
        }
        if (dto.getOrganization() != null) {
            entity.setOrganization(dto.getOrganization());
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
