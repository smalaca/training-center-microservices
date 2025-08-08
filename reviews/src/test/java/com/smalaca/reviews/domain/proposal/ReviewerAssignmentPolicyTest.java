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
    private static final UUID CATEGORY_ONE = id();
    private static final UUID CATEGORY_TWO = id();
    private static final UUID CATEGORY_THREE = id();
    private static final Set<UUID> PROPOSAL_CATEGORIES = Set.of(CATEGORY_ONE, CATEGORY_TWO, CATEGORY_THREE);
    private static final UUID AUTHOR_ID = id();
    private static final LocalDateTime NOW = LocalDateTime.now();
    
    private final Clock clock = mock(Clock.class);
    private final TrainersCatalogue trainersCatalogue = mock(TrainersCatalogue.class);
    private final ReviewerAssignmentPolicy policy = new ReviewerAssignmentPolicyFactory().reviewerAssignmentPolicy(clock, trainersCatalogue);

    @BeforeEach
    void setUp() {
        given(clock.now()).willReturn(NOW);
    }

    @Test
    void shouldAssignTrainerWhoSupportsAllCategories() {
        Trainer perfectTrainer = trainerWithCategories(CATEGORY_ONE, CATEGORY_TWO, CATEGORY_THREE, id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(perfectTrainer));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(perfectTrainer.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldAssignTrainerWithBestPartialMatchWhenNoPerfectMatch() {
        Trainer partialTrainerOne = trainerWithCategories(CATEGORY_ONE, id());
        Trainer partialTrainerTwo = trainerWithCategories(id(), CATEGORY_ONE, CATEGORY_TWO, id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainerOne, partialTrainerTwo));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(partialTrainerTwo.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldAssignFirstTrainerWhenMultipleHaveSamePartialMatch() {
        Trainer partialTrainerOne = trainerWithCategories(CATEGORY_ONE, id());
        Trainer partialTrainerTwo = trainerWithCategories(CATEGORY_TWO, id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainerOne, partialTrainerTwo));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(partialTrainerOne.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldPreferTrainerWithPerfectMatchOverPartialMatch() {
        Trainer partialTrainer = trainerWithCategories(CATEGORY_ONE, id(), id());
        Trainer perfectTrainer = trainerWithCategories(CATEGORY_ONE, CATEGORY_TWO, CATEGORY_THREE);
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(partialTrainer, perfectTrainer));

        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(perfectTrainer.id())
                .hasOccurredAt(NOW);
    }

    private Trainer trainerWithCategories(UUID... categories) {
        return new Trainer(id(), Set.of(categories), Set.of(id(), id(), id(), id()));
    }

    @Test
    void shouldAssignTrainerWithNoAssignments() {
        Trainer trainerWithNoAssignments = trainerWithAssignments();
        Trainer trainerWithAssignments = trainerWithAssignments(id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(trainerWithAssignments, trainerWithNoAssignments));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(trainerWithNoAssignments.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldAssignTrainerWithLowestWorkloadWhenNoTrainerHasZeroAssignments() {
        Trainer trainerWithOneAssignment = trainerWithAssignments(id());
        Trainer trainerWithTwoAssignments = trainerWithAssignments(id(), id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(trainerWithTwoAssignments, trainerWithOneAssignment));
        
        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(trainerWithOneAssignment.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldAssignTrainerWithTwoAssignmentsWhenOthersHaveThreeOrMore() {
        Trainer trainerWithTwoAssignments = trainerWithAssignments(id(), id());
        Trainer trainerWithThreeAssignments = trainerWithAssignments(id(), id(), id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(trainerWithThreeAssignments, trainerWithTwoAssignments));

        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(trainerWithTwoAssignments.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldAssignFirstTrainerWhenMultipleHaveSameLowestWorkload() {
        Trainer firstTrainerWithOneAssignment = trainerWithAssignments(id());
        Trainer secondTrainerWithOneAssignment = trainerWithAssignments(id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(firstTrainerWithOneAssignment, secondTrainerWithOneAssignment));

        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isAssigned()
                .hasReviewerId(firstTrainerWithOneAssignment.id())
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldBeQueuedWhenNotSpecializedTrainersWithReachedAssignmentsThreshold() {
        Trainer trainerWithThreeAssignments = trainerWithAssignments(id(), id(), id());
        Trainer trainerWithFourAssignments = trainerWithAssignments(id(), id(), id(), id());
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(trainerWithThreeAssignments, trainerWithFourAssignments));

        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isQueued()
                .hasNoReviewerId()
                .hasOccurredAt(NOW);
    }

    @Test
    void shouldBeQueuedWhenNoTrainersExist() {
        given(trainersCatalogue.findAllTrainers()).willReturn(emptyList());

        Assignment assignment = policy.assign(AUTHOR_ID, PROPOSAL_CATEGORIES);

        assertThatAssignment(assignment)
                .isQueued()
                .hasNoReviewerId()
                .hasOccurredAt(NOW);
    }

    private Trainer trainerWithAssignments(UUID... assignments) {
        return new Trainer(id(), Set.of(id(), id()), Set.of(assignments));
    }

    private static UUID id() {
        return randomUUID();
    }
}