package com.smalaca.trainingmanagement.application.proposal;

import com.smalaca.trainingmanagement.domain.program.TrainingProgram;
import com.smalaca.trainingmanagement.domain.program.TrainingProgramRepository;
import com.smalaca.trainingmanagement.domain.proposal.TrainingProposal;
import com.smalaca.trainingmanagement.domain.proposal.TrainingProposalRepository;
import com.smalaca.trainingmanagement.domain.proposal.TrainingProposalStatus;
import com.smalaca.trainingmanagement.infrastructure.kafka.EventPublisher;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TrainingProposalServiceTest {

    @Mock private TrainingProposalRepository trainingProposalRepository;
    @Mock private TrainingProgramRepository trainingProgramRepository;
    @Mock private EventPublisher eventPublisher;

    private TrainingProposalService service;

    @BeforeEach
    void setUp() {
        service = new TrainingProposalService(trainingProposalRepository, trainingProgramRepository, eventPublisher);
    }

    @Test
    void shouldGetAllProposals() {
        // given
        List<TrainingProposal> proposals = List.of(
                createProposal("Java Programming"),
                createProposal("Python Programming")
        );
        given(trainingProposalRepository.findAll()).willReturn(proposals);

        // when
        List<TrainingProposal> result = service.getAllProposals();

        // then
        assertThat(result).isEqualTo(proposals);
    }

    @Test
    void shouldGetProposalById() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProposal proposal = createProposal("Java Programming");
        given(trainingProposalRepository.findById(id)).willReturn(Optional.of(proposal));

        // when
        TrainingProposal result = service.getProposal(id);

        // then
        assertThat(result).isEqualTo(proposal);
    }

    @Test
    void shouldThrowExceptionWhenProposalNotFound() {
        // given
        UUID id = UUID.randomUUID();
        given(trainingProposalRepository.findById(id)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> service.getProposal(id));

        // then
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Training proposal not found with id: " + id);
    }

    @Test
    void shouldCreateProposal() {
        // given
        String title = "Java Programming";
        String description = "Learn Java programming from scratch";
        String category = "Programming";
        String level = "Beginner";
        String trainer = "John Doe";
        int durationInDays = 5;

        TrainingProposal savedProposal = createProposal(title);
        given(trainingProposalRepository.save(any(TrainingProposal.class))).willReturn(savedProposal);

        // when
        TrainingProposal result = service.createProposal(title, description, category, level, trainer, durationInDays);

        // then
        assertThat(result).isEqualTo(savedProposal);

        ArgumentCaptor<TrainingProposal> proposalCaptor = ArgumentCaptor.forClass(TrainingProposal.class);
        verify(trainingProposalRepository).save(proposalCaptor.capture());

        TrainingProposal capturedProposal = proposalCaptor.getValue();
        assertThat(capturedProposal.getTitle()).isEqualTo(title);
        assertThat(capturedProposal.getDescription()).isEqualTo(description);
        assertThat(capturedProposal.getCategory()).isEqualTo(category);
        assertThat(capturedProposal.getLevel()).isEqualTo(level);
        assertThat(capturedProposal.getTrainer()).isEqualTo(trainer);
        assertThat(capturedProposal.getDurationInDays()).isEqualTo(durationInDays);
        assertThat(capturedProposal.getStatus()).isEqualTo(TrainingProposalStatus.PENDING);
    }

    @Test
    void shouldUpdateProposal() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProposal proposal = createProposal("Java Programming");
        given(trainingProposalRepository.findById(id)).willReturn(Optional.of(proposal));
        given(trainingProposalRepository.save(proposal)).willReturn(proposal);

        String newTitle = "Advanced Java Programming";
        String newDescription = "Learn advanced Java programming";
        String newCategory = "Advanced Programming";
        String newLevel = "Advanced";
        String newTrainer = "Jane Smith";
        int newDurationInDays = 10;

        // when
        TrainingProposal result = service.updateProposal(id, newTitle, newDescription, newCategory, newLevel, newTrainer, newDurationInDays);

        // then
        assertThat(result).isEqualTo(proposal);
        assertThat(proposal.getTitle()).isEqualTo(newTitle);
        assertThat(proposal.getDescription()).isEqualTo(newDescription);
        assertThat(proposal.getCategory()).isEqualTo(newCategory);
        assertThat(proposal.getLevel()).isEqualTo(newLevel);
        assertThat(proposal.getTrainer()).isEqualTo(newTrainer);
        assertThat(proposal.getDurationInDays()).isEqualTo(newDurationInDays);
    }

    @Test
    void shouldAcceptProposal() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProposal proposal = createProposal("Java Programming");
        given(trainingProposalRepository.findById(id)).willReturn(Optional.of(proposal));
        given(trainingProposalRepository.save(proposal)).willReturn(proposal);

        TrainingProgram savedProgram = new TrainingProgram(
                proposal.getId(),
                proposal.getTitle(),
                proposal.getDescription(),
                proposal.getCategory(),
                proposal.getLevel(),
                proposal.getTrainer(),
                proposal.getDurationInDays()
        );
        given(trainingProgramRepository.save(any(TrainingProgram.class))).willReturn(savedProgram);

        // when
        TrainingProgram result = service.acceptProposal(id);

        // then
        assertThat(result).isEqualTo(savedProgram);
        assertThat(proposal.getStatus()).isEqualTo(TrainingProposalStatus.ACCEPTED);

        ArgumentCaptor<TrainingProgram> programCaptor = ArgumentCaptor.forClass(TrainingProgram.class);
        verify(trainingProgramRepository).save(programCaptor.capture());

        TrainingProgram capturedProgram = programCaptor.getValue();
        assertThat(capturedProgram.getId()).isEqualTo(proposal.getId());
        assertThat(capturedProgram.getTitle()).isEqualTo(proposal.getTitle());
        assertThat(capturedProgram.getDescription()).isEqualTo(proposal.getDescription());
        assertThat(capturedProgram.getCategory()).isEqualTo(proposal.getCategory());
        assertThat(capturedProgram.getLevel()).isEqualTo(proposal.getLevel());
        assertThat(capturedProgram.getTrainer()).isEqualTo(proposal.getTrainer());
        assertThat(capturedProgram.getDurationInDays()).isEqualTo(proposal.getDurationInDays());
        assertThat(capturedProgram.isActive()).isTrue();

        verify(eventPublisher).publishTrainingProposalAccepted(proposal);
    }

    @Test
    void shouldRejectProposal() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProposal proposal = createProposal("Java Programming");
        given(trainingProposalRepository.findById(id)).willReturn(Optional.of(proposal));
        given(trainingProposalRepository.save(proposal)).willReturn(proposal);

        // when
        service.rejectProposal(id);

        // then
        assertThat(proposal.getStatus()).isEqualTo(TrainingProposalStatus.REJECTED);
        verify(trainingProposalRepository).save(proposal);
        verifyNoInteractions(trainingProgramRepository);
        verifyNoInteractions(eventPublisher);
    }

    private TrainingProposal createProposal(String title) {
        return new TrainingProposal(
                title,
                "Description of " + title,
                "Programming",
                "Beginner",
                "John Doe",
                5
        );
    }
}