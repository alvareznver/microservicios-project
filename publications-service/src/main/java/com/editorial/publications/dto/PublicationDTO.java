package com.editorial.publications.dto;

import com.editorial.publications.entity.PublicationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicationDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200)
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Author ID is required")
    @Positive(message = "Author ID must be positive")
    private Long authorId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    private PublicationStatus status = PublicationStatus.DRAFT;

    @Size(max = 500)
    private String reviewComments;

    @Size(max = 100)
    private String editorName;

    @Size(max = 500)
    private String rejectionReason;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    // For enriched response
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private AuthorInfoDTO author;
}
