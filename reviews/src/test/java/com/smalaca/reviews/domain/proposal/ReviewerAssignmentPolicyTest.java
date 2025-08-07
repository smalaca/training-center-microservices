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
    private static final UUID CATEGORY_ONE = randomUUID();
    private static final UUID CATEGORY_TWO = randomUUID();
    private static final UUID CATEGORY_THREE = randomUUID();
    private static final Set<UUID> PROPOSAL_CATEGORIES = Set.of(CATEGORY_ONE, CATEGORY_TWO, CATEGORY_THREE);
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

        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isQueued()
                .hasNoReviewerId()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldAssignTrainerWhoSupportsAllCategories() {
        Trainer perfectTrainer = trainer(CATEGORY_ONE, CATEGORY_TWO, CATEGORY_THREE, randomUUID());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(perfectTrainer));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(perfectTrainer.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldAssignBestPartialMatchWhenNoPerfectMatch() {
        Trainer partialTrainerOne = trainer(CATEGORY_ONE, randomUUID());
        Trainer partialTrainerTwo = trainer(randomUUID(), CATEGORY_ONE, CATEGORY_TWO, randomUUID());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainerOne, partialTrainerTwo));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(partialTrainerTwo.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldAssignFirstTrainerWhenMultipleHaveSamePartialMatch() {
        Trainer partialTrainerOne = trainer(CATEGORY_ONE, randomUUID());
        Trainer partialTrainerTwo = trainer(CATEGORY_TWO, randomUUID());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainerOne, partialTrainerTwo));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(partialTrainerOne.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldDelegateToFallbackWhenNoTrainerSupportsAnyCategory() {
        Trainer unrelatedTrainer = trainer(randomUUID(), randomUUID());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(unrelatedTrainer));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isQueued()
                .hasNoReviewerId()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldDelegateToFallbackWhenNoTrainersExist() {
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of());
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isQueued()
                .hasNoReviewerId()
                .hasOccurredAt(NOW);
    }

    @Test
    void specializationAssignmentPolicyShouldPreferPerfectMatchOverPartialMatch() {
        Trainer partialTrainer = trainer(CATEGORY_ONE, randomUUID(), randomUUID());
        Trainer perfectTrainer = trainer(CATEGORY_ONE, CATEGORY_TWO, CATEGORY_THREE);
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainer, perfectTrainer));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(perfectTrainer.id())
                .hasOccurredAt(NOW);
    }

    private Trainer trainer(UUID... categories) {
        return new Trainer(randomUUID(), Set.of(categories));
    }
}