package com.smalaca.trainingprograms.infrastructure.api.eventlistener.spring;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingprograms.application.trainingprogram.TrainingProgramApplicationService;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TrainingProgramEventListener {
    private final TrainingProgramApplicationService trainingProgramApplicationService;

    TrainingProgramEventListener(TrainingProgramApplicationService trainingProgramApplicationService) {
        this.trainingProgramApplicationService = trainingProgramApplicationService;
    }

    @EventListener
    @DrivingAdapter
    public void listen(TrainingProgramReleasedEvent event) {
        trainingProgramApplicationService.create(event);
    }
}