package com.editorial.authors.repository;

import com.editorial.authors.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByEmail(String email);

    Page<Author> findByActive(Boolean active, Pageable pageable);

    boolean existsByEmail(String email);
}
