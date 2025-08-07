package com.smalaca.reviews.domain.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.AssignmentAssertion.assertThatAssignment;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ReviewerAssignmentPolicyTest {
    private static final UUID AUTHOR_ID = randomUUID();
    private static final LocalDateTime NOW = LocalDateTime.now();
    
    private final Clock clock = mock(Clock.class);
    private final TrainersCatalogue trainersCatalogue = mock(TrainersCatalogue.class);
    private final ReviewerAssignmentPolicy policy = new ReviewerAssignmentPolicyFactory().reviewerAssignmentPolicy(clock, trainersCatalogue);

    @BeforeEach
    void setUp() {
        given(clock.now()).willReturn(NOW);
    }

    @Test
    void noAssignmentPolicyShouldAlwaysReturnQueuedAssignmentWithNullReviewer() {
        given(trainersCatalogue.findAllTrainers()).willReturn(emptyList());
        Set<UUID> categories = Set.of(randomUUID(), randomUUID());

        Assignment assignment = policy.assign(AUTHOR_ID, categories);

        assertThatAssignment(assignment)
                .hasNoReviewerId()
                .isQueued()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldAssignTrainerWhoSupportsAllCategories() {
        UUID trainerId = randomUUID();
        UUID category1 = randomUUID();
        UUID category2 = randomUUID();
        Set<UUID> requiredCategories = Set.of(category1, category2);
        
        Trainer perfectTrainer = new Trainer(trainerId, Set.of(category1, category2, randomUUID()));
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(perfectTrainer));
        
        Assignment assignment = policy.assign(AUTHOR_ID, requiredCategories);

        assertThatAssignment(assignment)
                .hasReviewerId(trainerId)
                .isAssigned()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldAssignBestPartialMatchWhenNoPerfectMatch() {
        UUID trainerId1 = randomUUID();
        UUID trainerId2 = randomUUID();
        UUID category1 = randomUUID();
        UUID category2 = randomUUID();
        UUID category3 = randomUUID();
        Set<UUID> requiredCategories = Set.of(category1, category2, category3);
        
        Trainer partialTrainer1 = new Trainer(trainerId1, Set.of(category1)); // matches 1
        Trainer partialTrainer2 = new Trainer(trainerId2, Set.of(category1, category2)); // matches 2 - best
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainer1, partialTrainer2));
        
        Assignment assignment = policy.assign(AUTHOR_ID, requiredCategories);

        assertThatAssignment(assignment)
                .hasReviewerId(trainerId2)
                .isAssigned()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldAssignFirstTrainerWhenMultipleHaveSamePartialMatch() {
        UUID trainerId1 = randomUUID();
        UUID trainerId2 = randomUUID();
        UUID category1 = randomUUID();
        UUID category2 = randomUUID();
        UUID category3 = randomUUID();
        Set<UUID> requiredCategories = Set.of(category1, category2, category3);
        
        Trainer partialTrainer1 = new Trainer(trainerId1, Set.of(category1, category2)); // matches 2 - first
        Trainer partialTrainer2 = new Trainer(trainerId2, Set.of(category2, category3)); // matches 2 - second
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainer1, partialTrainer2));
        
        Assignment assignment = policy.assign(AUTHOR_ID, requiredCategories);

        assertThatAssignment(assignment)
                .hasReviewerId(trainerId1)
                .isAssigned()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldDelegateToFallbackWhenNoTrainerSupportsAnyCategory() {
        UUID category1 = randomUUID();
        UUID category2 = randomUUID();
        Set<UUID> requiredCategories = Set.of(category1, category2);
        
        Trainer unrelatedTrainer = new Trainer(randomUUID(), Set.of(randomUUID(), randomUUID()));
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(unrelatedTrainer));
        
        Assignment assignment = policy.assign(AUTHOR_ID, requiredCategories);

        // Should delegate to NoAssignmentPolicy
        assertThatAssignment(assignment)
                .hasNoReviewerId()
                .isQueued()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldDelegateToFallbackWhenNoTrainersExist() {
        Set<UUID> requiredCategories = Set.of(randomUUID(), randomUUID());
        
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of());
        
        Assignment assignment = policy.assign(AUTHOR_ID, requiredCategories);

        // Should delegate to NoAssignmentPolicy
        assertThatAssignment(assignment)
                .hasNoReviewerId()
                .isQueued()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldPreferPerfectMatchOverPartialMatch() {
        UUID perfectTrainerId = randomUUID();
        UUID partialTrainerId = randomUUID();
        UUID category1 = randomUUID();
        UUID category2 = randomUUID();
        Set<UUID> requiredCategories = Set.of(category1, category2);
        
        Trainer partialTrainer = new Trainer(partialTrainerId, Set.of(category1, randomUUID(), randomUUID()));
        Trainer perfectTrainer = new Trainer(perfectTrainerId, Set.of(category1, category2));
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainer, perfectTrainer));
        
        Assignment assignment = policy.assign(AUTHOR_ID, requiredCategories);

        assertThatAssignment(assignment)
                .hasReviewerId(perfectTrainerId)
                .isAssigned()
                .hasOccurredAt(NOW);
    }
}