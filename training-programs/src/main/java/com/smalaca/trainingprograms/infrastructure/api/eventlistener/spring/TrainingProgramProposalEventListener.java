package com.smalaca.trainingprograms.infrastructure.api.eventlistener.spring;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposalApplicationService;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TrainingProgramProposalEventListener {
    private final TrainingProgramProposalApplicationService trainingProgramProposalApplicationService;

    TrainingProgramProposalEventListener(TrainingProgramProposalApplicationService trainingProgramProposalApplicationService) {
        this.trainingProgramProposalApplicationService = trainingProgramProposalApplicationService;
    }

    @EventListener
    @DrivingAdapter
    public void listen(TrainingProgramProposedEvent event) {
        trainingProgramProposalApplicationService.create(event);
    }

    @EventListener
    @DrivingAdapter
    public void listen(TrainingProgramReleasedEvent event) {
        trainingProgramProposalApplicationService.apply(event);
    }
}