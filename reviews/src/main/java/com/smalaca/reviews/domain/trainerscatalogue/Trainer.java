package com.smalaca.reviews.domain.trainerscatalogue;

import java.util.Set;
import java.util.UUID;

public record Trainer(UUID id, Set<UUID> categoryIds, Set<UUID> assignmentsIds) {
}
