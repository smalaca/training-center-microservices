package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Policy;

import java.util.Set;
import java.util.UUID;

@Policy
public interface ReviewerAssignmentPolicy {
    Assignment assign(UUID authorId, Set<UUID> categoriesIds);
}