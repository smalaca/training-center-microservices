package com.smalaca.trainingmanagement.domain.proposal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class TrainingProposalTest {

    @Test
    void shouldCreateTrainingProposalWithPendingStatus() {
        // given
        String title = "Java Programming";
        String description = "Learn Java programming from scratch";
        String category = "Programming";
        String level = "Beginner";
        String trainer = "John Doe";
        int durationInDays = 5;

        // when
        TrainingProposal proposal = new TrainingProposal(title, description, category, level, trainer, durationInDays);

        // then
        assertThat(proposal.getId()).isNotNull();
        assertThat(proposal.getTitle()).isEqualTo(title);
        assertThat(proposal.getDescription()).isEqualTo(description);
        assertThat(proposal.getCategory()).isEqualTo(category);
        assertThat(proposal.getLevel()).isEqualTo(level);
        assertThat(proposal.getTrainer()).isEqualTo(trainer);
        assertThat(proposal.getDurationInDays()).isEqualTo(durationInDays);
        assertThat(proposal.getStatus()).isEqualTo(TrainingProposalStatus.PENDING);
    }

    @Test
    void shouldUpdateTrainingProposalWhenStatusIsPending() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        
        String newTitle = "Advanced Java Programming";
        String newDescription = "Learn advanced Java programming";
        String newCategory = "Advanced Programming";
        String newLevel = "Advanced";
        String newTrainer = "Jane Smith";
        int newDurationInDays = 10;

        // when
        proposal.update(newTitle, newDescription, newCategory, newLevel, newTrainer, newDurationInDays);

        // then
        assertThat(proposal.getTitle()).isEqualTo(newTitle);
        assertThat(proposal.getDescription()).isEqualTo(newDescription);
        assertThat(proposal.getCategory()).isEqualTo(newCategory);
        assertThat(proposal.getLevel()).isEqualTo(newLevel);
        assertThat(proposal.getTrainer()).isEqualTo(newTrainer);
        assertThat(proposal.getDurationInDays()).isEqualTo(newDurationInDays);
        assertThat(proposal.getStatus()).isEqualTo(TrainingProposalStatus.PENDING);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAcceptedProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        proposal.accept();

        // when
        Throwable thrown = catchThrowable(() -> 
                proposal.update("New Title", "New Description", "New Category", "New Level", "New Trainer", 10));

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update a proposal that is not in PENDING status");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRejectedProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        proposal.reject();

        // when
        Throwable thrown = catchThrowable(() -> 
                proposal.update("New Title", "New Description", "New Category", "New Level", "New Trainer", 10));

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update a proposal that is not in PENDING status");
    }

    @Test
    void shouldAcceptProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);

        // when
        proposal.accept();

        // then
        assertThat(proposal.getStatus()).isEqualTo(TrainingProposalStatus.ACCEPTED);
    }

    @Test
    void shouldThrowExceptionWhenAcceptingAlreadyAcceptedProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        proposal.accept();

        // when
        Throwable thrown = catchThrowable(proposal::accept);

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot accept a proposal that is not in PENDING status");
    }

    @Test
    void shouldThrowExceptionWhenAcceptingRejectedProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        proposal.reject();

        // when
        Throwable thrown = catchThrowable(proposal::accept);

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot accept a proposal that is not in PENDING status");
    }

    @Test
    void shouldRejectProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);

        // when
        proposal.reject();

        // then
        assertThat(proposal.getStatus()).isEqualTo(TrainingProposalStatus.REJECTED);
    }

    @Test
    void shouldThrowExceptionWhenRejectingAlreadyRejectedProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        proposal.reject();

        // when
        Throwable thrown = catchThrowable(proposal::reject);

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot reject a proposal that is not in PENDING status");
    }

    @Test
    void shouldThrowExceptionWhenRejectingAcceptedProposal() {
        // given
        TrainingProposal proposal = new TrainingProposal(
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        proposal.accept();

        // when
        Throwable thrown = catchThrowable(proposal::reject);

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot reject a proposal that is not in PENDING status");
    }
}