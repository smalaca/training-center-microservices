package com.smalaca.trainingprograms.infrastructure.api.rest.trainingprogramproposal;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposalApplicationService;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.query.trainingprogramproposal.TrainingProgramProposalQueryService;
import com.smalaca.trainingprograms.query.trainingprogramproposal.TrainingProgramProposalView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("trainingprogramproposal")
public class TrainingProgramProposalRestController {
    private final TrainingProgramProposalApplicationService trainingProgramProposalApplicationService;
    private final TrainingProgramProposalQueryService queryService;

    @Autowired
    TrainingProgramProposalRestController(
            TrainingProgramProposalApplicationService applicationService,
            TrainingProgramProposalQueryService queryService) {
        this.trainingProgramProposalApplicationService = applicationService;
        this.queryService = queryService;
    }

    @PostMapping
    @DrivingAdapter
    public ResponseEntity<Void> propose(@RequestBody CreateTrainingProgramProposalCommand command) {
        trainingProgramProposalApplicationService.propose(command);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{trainingProgramProposalId}")
    public ResponseEntity<TrainingProgramProposalView> findById(@PathVariable UUID trainingProgramProposalId) {
        Optional<TrainingProgramProposalView> found = queryService.findById(trainingProgramProposalId);

        return found
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<TrainingProgramProposalView> findAll() {
        return queryService.findAll();
    }
}