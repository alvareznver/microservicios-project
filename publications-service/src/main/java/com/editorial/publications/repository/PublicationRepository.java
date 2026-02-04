package com.editorial.publications.repository;

import com.editorial.publications.entity.Publication;
import com.editorial.publications.entity.PublicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findByAuthorId(Long authorId);

    Page<Publication> findByStatus(PublicationStatus status, Pageable pageable);

    Page<Publication> findByAuthorId(Long authorId, Pageable pageable);

    long countByStatus(PublicationStatus status);
}
