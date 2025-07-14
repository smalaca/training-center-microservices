package com.smalaca.reviews.domain.proposal;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProposalTestDto(
        UUID proposalId, UUID authorId, String title, String content, UUID correlationId, LocalDateTime registeredAt,
        UUID reviewedById, LocalDateTime reviewedAt, ProposalStatus status) {
}