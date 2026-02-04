package com.editorial.publications.service;

import com.editorial.publications.client.AuthorServiceClient;
import com.editorial.publications.dto.PublicationDTO;
import com.editorial.publications.entity.Publication;
import com.editorial.publications.entity.PublicationStatus;
import com.editorial.publications.exception.AuthorNotFoundException;
import com.editorial.publications.exception.PublicationInvalidStateException;
import com.editorial.publications.exception.PublicationNotFoundException;
import com.editorial.publications.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Publication Service - Orchestrates publication operations
 * Strategy pattern: Uses different validation strategies for status changes
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;
    private final AuthorServiceClient authorServiceClient;
    private final PublicationStatusValidator statusValidator;

    /**
     * Create a new publication
     * Validates author existence via Authors Service
     */
    public PublicationDTO createPublication(PublicationDTO dto) {
        // Validate author exists (inter-service dependency)
        if (!authorServiceClient.authorExists(dto.getAuthorId())) {
            throw new AuthorNotFoundException("Author not found with id: " + dto.getAuthorId());
        }

        Publication publication = publicationMapper.dtoToEntity(dto);
        Publication saved = publicationRepository.save(publication);
        
        log.info("Publication created: id={}, title={}, authorId={}", 
                 saved.getId(), saved.getTitle(), saved.getAuthorId());
        
        return enrichPublication(publicationMapper.entityToDTO(saved));
    }

    /**
     * Get publication by ID with enriched author information
     */
    @Transactional(readOnly = true)
    public PublicationDTO getPublicationById(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new PublicationNotFoundException("Publication not found with id: " + id));
        return enrichPublication(publicationMapper.entityToDTO(publication));
    }

    /**
     * List all publications with pagination
     */
    @Transactional(readOnly = true)
    public Page<PublicationDTO> listPublications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return publicationRepository.findAll(pageable)
                .map(this::enrichPublication)
                .map(publicationMapper::toDTO);
    }

    /**
     * List publications by author
     */
    @Transactional(readOnly = true)
    public Page<PublicationDTO> listPublicationsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return publicationRepository.findByAuthorId(authorId, pageable)
                .map(this::enrichPublication)
                .map(publicationMapper::toDTO);
    }

    /**
     * Change publication status with validation
     */
    public PublicationDTO changeStatus(Long id, PublicationStatus newStatus) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new PublicationNotFoundException("Publication not found with id: " + id));

        // Validate status transition
        if (!publication.canChangeStatus(newStatus)) {
            throw new PublicationInvalidStateException(
                    "Cannot change publication status from " + publication.getStatus() + " to " + newStatus);
        }

        // Use strategy validator for additional business rules
        statusValidator.validate(publication, newStatus);

        publication.setStatus(newStatus);
        Publication updated = publicationRepository.save(publication);
        
        log.info("Publication status changed: id={}, newStatus={}", id, newStatus);
        
        return enrichPublication(publicationMapper.entityToDTO(updated));
    }

    /**
     * Enrich publication with author information
     */
    private PublicationDTO enrichPublication(PublicationDTO dto) {
        try {
            var authorInfo = authorServiceClient.getAuthorInfo(dto.getAuthorId());
            dto.setAuthor(authorInfo);
        } catch (Exception e) {
            log.warn("Could not enrich publication with author info: {}", e.getMessage());
            // Continue without author info
        }
        return dto;
    }

    /**
     * Helper method to convert entity to DTO (internal use)
     */
    private PublicationDTO enrichPublication(Publication publication) {
        PublicationDTO dto = publicationMapper.entityToDTO(publication);
        return enrichPublication(dto);
    }
}
