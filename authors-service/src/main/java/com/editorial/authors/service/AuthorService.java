package com.editorial.authors.service;

import com.editorial.authors.dto.AuthorDTO;
import com.editorial.authors.entity.Author;
import com.editorial.authors.exception.AuthorAlreadyExistsException;
import com.editorial.authors.exception.AuthorNotFoundException;
import com.editorial.authors.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    /**
     * Create a new author
     */
    public AuthorDTO createAuthor(AuthorDTO dto) {
        if (authorRepository.existsByEmail(dto.getEmail())) {
            throw new AuthorAlreadyExistsException("Author with email " + dto.getEmail() + " already exists");
        }

        Author author = authorMapper.dtoToEntity(dto);
        Author savedAuthor = authorRepository.save(author);
        log.info("Author created: id={}, email={}", savedAuthor.getId(), savedAuthor.getEmail());
        return authorMapper.entityToDTO(savedAuthor);
    }

    /**
     * Get author by ID
     */
    @Transactional(readOnly = true)
    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));
        return authorMapper.entityToDTO(author);
    }

    /**
     * List all authors with pagination
     */
    @Transactional(readOnly = true)
    public Page<AuthorDTO> listAuthors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return authorRepository.findAll(pageable)
                .map(authorMapper::entityToDTO);
    }

    /**
     * Update an author
     */
    public AuthorDTO updateAuthor(Long id, AuthorDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));

        authorMapper.updateEntityFromDTO(dto, author);
        Author updatedAuthor = authorRepository.save(author);
        log.info("Author updated: id={}", id);
        return authorMapper.entityToDTO(updatedAuthor);
    }

    /**
     * Delete an author (soft delete)
     */
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));
        author.setActive(false);
        authorRepository.save(author);
        log.info("Author deactivated: id={}", id);
    }

    /**
     * Check if author exists (for inter-service calls)
     */
    @Transactional(readOnly = true)
    public boolean authorExists(Long id) {
        return authorRepository.existsById(id);
    }
}
