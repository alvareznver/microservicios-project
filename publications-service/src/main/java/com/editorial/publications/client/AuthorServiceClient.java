package com.editorial.publications.client;

import com.editorial.publications.dto.AuthorInfoDTO;
import com.editorial.publications.exception.AuthorServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Adapter pattern: Adapts external Authors Service to Publications Service needs
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorServiceClient {

    private final RestTemplate restTemplate;

    @Value("${authors.service.url}")
    private String authorsServiceUrl;

    @Value("${authors.service.timeout:5000}")
    private long timeout;

    /**
     * Verify if author exists in Authors Service
     */
    public boolean authorExists(Long authorId) {
        try {
            String url = authorsServiceUrl + "/authors/" + authorId + "/exists";
            Boolean exists = restTemplate.getForObject(url, Boolean.class);
            return exists != null && exists;
        } catch (RestClientException e) {
            log.warn("Failed to verify author existence for id: {}", authorId, e);
            throw new AuthorServiceException("Unable to verify author with id: " + authorId, e);
        }
    }

    /**
     * Get author information from Authors Service
     */
    public AuthorInfoDTO getAuthorInfo(Long authorId) {
        try {
            String url = authorsServiceUrl + "/authors/" + authorId;
            return restTemplate.getForObject(url, AuthorInfoDTO.class);
        } catch (RestClientException e) {
            log.warn("Failed to fetch author info for id: {}", authorId, e);
            throw new AuthorServiceException("Unable to fetch author with id: " + authorId, e);
        }
    }
}
