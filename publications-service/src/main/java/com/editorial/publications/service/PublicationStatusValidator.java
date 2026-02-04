package com.editorial.publications.service;

import com.editorial.publications.entity.Publication;
import com.editorial.publications.entity.PublicationStatus;
import com.editorial.publications.exception.PublicationInvalidStateException;
import org.springframework.stereotype.Component;

/**
 * Strategy pattern: Encapsulates validation strategies for status changes
 */
@Component
public class PublicationStatusValidator {

    /**
     * Validates if a status change is allowed according to business rules
     */
    public void validate(Publication publication, PublicationStatus newStatus) {
        switch (newStatus) {
            case IN_REVIEW:
                validateDraftToReview(publication);
                break;
            case APPROVED:
                validateReviewToApproved(publication);
                break;
            case REJECTED:
                validateReviewToRejected(publication);
                break;
            case PUBLISHED:
                validateApprovedToPublished(publication);
                break;
            case REQUIRES_CHANGES:
                validateReviewToRequiresChanges(publication);
                break;
            default:
                break;
        }
    }

    private void validateDraftToReview(Publication publication) {
        if (publication.getContent() == null || publication.getContent().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Publication content cannot be empty");
        }
    }

    private void validateReviewToApproved(Publication publication) {
        if (publication.getEditorName() == null || publication.getEditorName().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Editor name is required for approval");
        }
    }

    private void validateReviewToRejected(Publication publication) {
        if (publication.getRejectionReason() == null || publication.getRejectionReason().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Rejection reason is required");
        }
    }

    private void validateApprovedToPublished(Publication publication) {
        // All approved publications can be published
    }

    private void validateReviewToRequiresChanges(Publication publication) {
        if (publication.getReviewComments() == null || publication.getReviewComments().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Review comments are required when requesting changes");
        }
    }
}
