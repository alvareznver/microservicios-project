package com.editorial.publications.service;

import com.editorial.publications.dto.PublicationDTO;
import com.editorial.publications.entity.Publication;
import org.springframework.stereotype.Component;

@Component
public class PublicationMapper {

    public PublicationDTO entityToDTO(Publication entity) {
        if (entity == null) {
            return null;
        }

        return PublicationDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorId(entity.getAuthorId())
                .status(entity.getStatus())
                .reviewComments(entity.getReviewComments())
                .editorName(entity.getEditorName())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Publication dtoToEntity(PublicationDTO dto) {
        if (dto == null) {
            return null;
        }

        return Publication.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .authorId(dto.getAuthorId())
                .status(dto.getStatus() != null ? dto.getStatus() : Publication.builder().build().getStatus())
                .reviewComments(dto.getReviewComments())
                .editorName(dto.getEditorName())
                .rejectionReason(dto.getRejectionReason())
                .build();
    }

    public PublicationDTO toDTO(PublicationDTO dto) {
        return dto;
    }
}
