package com.editorial.publications.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PublicationStatus status = PublicationStatus.DRAFT;

    @Column(length = 500)
    private String reviewComments;

    @Column(length = 100)
    private String editorName;

    @Column(length = 500)
    private String rejectionReason;

    public boolean canChangeStatus(PublicationStatus newStatus) {
        // Define transition rules
        return switch (this.status) {
            case DRAFT -> newStatus == PublicationStatus.IN_REVIEW;
            case IN_REVIEW -> newStatus == PublicationStatus.APPROVED || 
                            newStatus == PublicationStatus.REJECTED ||
                            newStatus == PublicationStatus.REQUIRES_CHANGES;
            case REQUIRES_CHANGES -> newStatus == PublicationStatus.IN_REVIEW;
            case APPROVED -> newStatus == PublicationStatus.PUBLISHED;
            case PUBLISHED, REJECTED -> false; // Final states
        };
    }
}
