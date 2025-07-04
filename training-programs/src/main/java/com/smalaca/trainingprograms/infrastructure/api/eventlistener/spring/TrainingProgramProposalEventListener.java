package com.smalaca.trainingprograms.infrastructure.api.eventlistener.spring;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposalApplicationService;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TrainingProgramProposalEventListener {
    private final TrainingProgramProposalApplicationService trainingProgramProposalApplicationService;

    TrainingProgramProposalEventListener(TrainingProgramProposalApplicationService trainingProgramProposalApplicationService) {
        this.trainingProgramProposalApplicationService = trainingProgramProposalApplicationService;
    }

    @EventListener
    @DrivenAdapter
    public void listen(TrainingProgramProposedEvent event) {
        trainingProgramProposalApplicationService.create(event);
    }
}