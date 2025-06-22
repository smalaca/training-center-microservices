package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

@DomainRepository
@DrivenPort
public interface TrainingProgramProposalRepository {
    void save(TrainingProgramProposal trainingProgramProposal);
}