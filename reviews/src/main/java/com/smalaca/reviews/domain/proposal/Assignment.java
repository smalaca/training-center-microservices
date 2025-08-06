package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.ValueObject;

import java.time.LocalDateTime;
import java.util.UUID;

@ValueObject
public record Assignment(UUID reviewerId, ProposalStatus status, LocalDateTime occurredAt) {
}