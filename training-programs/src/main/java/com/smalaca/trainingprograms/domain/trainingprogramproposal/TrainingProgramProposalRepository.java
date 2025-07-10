package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

import java.util.Optional;
import java.util.UUID;

@DomainRepository
@DrivenPort
public interface TrainingProgramProposalRepository {
    void save(TrainingProgramProposal trainingProgramProposal);

    Optional<TrainingProgramProposal> findById(UUID trainingProgramProposalId);
}
