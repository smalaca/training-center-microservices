package com.smalaca.trainingmanagement.application.proposal;

import com.smalaca.trainingmanagement.domain.program.TrainingProgram;
import com.smalaca.trainingmanagement.domain.program.TrainingProgramRepository;
import com.smalaca.trainingmanagement.domain.proposal.TrainingProposal;
import com.smalaca.trainingmanagement.domain.proposal.TrainingProposalRepository;
import com.smalaca.trainingmanagement.infrastructure.kafka.EventPublisher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingProposalService {
    private final TrainingProposalRepository trainingProposalRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final EventPublisher eventPublisher;

    public List<TrainingProposal> getAllProposals() {
        return trainingProposalRepository.findAll();
    }

    public TrainingProposal getProposal(UUID id) {
        return trainingProposalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training proposal not found with id: " + id));
    }

    @Transactional
    public TrainingProposal createProposal(String title, String description, String category, String level, String trainer, int durationInDays) {
        TrainingProposal proposal = new TrainingProposal(title, description, category, level, trainer, durationInDays);
        return trainingProposalRepository.save(proposal);
    }

    @Transactional
    public TrainingProposal updateProposal(UUID id, String title, String description, String category, String level, String trainer, int durationInDays) {
        TrainingProposal proposal = getProposal(id);
        proposal.update(title, description, category, level, trainer, durationInDays);
        return trainingProposalRepository.save(proposal);
    }

    @Transactional
    public TrainingProgram acceptProposal(UUID id) {
        TrainingProposal proposal = getProposal(id);
        UUID programId = proposal.accept();
        
        TrainingProgram program = new TrainingProgram(
                programId,
                proposal.getTitle(),
                proposal.getDescription(),
                proposal.getCategory(),
                proposal.getLevel(),
                proposal.getTrainer(),
                proposal.getDurationInDays()
        );
        
        trainingProposalRepository.save(proposal);
        TrainingProgram savedProgram = trainingProgramRepository.save(program);
        
        eventPublisher.publishTrainingProposalAccepted(proposal);
        
        return savedProgram;
    }

    @Transactional
    public void rejectProposal(UUID id) {
        TrainingProposal proposal = getProposal(id);
        proposal.reject();
        trainingProposalRepository.save(proposal);
    }
}