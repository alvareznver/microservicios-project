package com.editorial.publications.controller;

import com.editorial.publications.dto.PublicationDTO;
import com.editorial.publications.entity.PublicationStatus;
import com.editorial.publications.service.PublicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publications")
@Slf4j
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @PostMapping
    public ResponseEntity<PublicationDTO> createPublication(@Valid @RequestBody PublicationDTO dto) {
        log.info("Creating publication: {}", dto.getTitle());
        PublicationDTO created = publicationService.createPublication(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPublication(@PathVariable Long id) {
        log.info("Fetching publication: {}", id);
        PublicationDTO publication = publicationService.getPublicationById(id);
        return ResponseEntity.ok(publication);
    }

    @GetMapping
    public ResponseEntity<Page<PublicationDTO>> listPublications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Listing publications - page: {}, size: {}", page, size);
        Page<PublicationDTO> publications = publicationService.listPublications(page, size);
        return ResponseEntity.ok(publications);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<PublicationDTO>> listByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Listing publications by author: {} - page: {}, size: {}", authorId, page, size);
        Page<PublicationDTO> publications = publicationService.listPublicationsByAuthor(authorId, page, size);
        return ResponseEntity.ok(publications);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PublicationDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam PublicationStatus status) {
        log.info("Changing publication status: id={}, newStatus={}", id, status);
        PublicationDTO updated = publicationService.changeStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
