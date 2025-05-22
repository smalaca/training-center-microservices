package com.smalaca.trainingmanagement.api.proposal;

import com.smalaca.trainingmanagement.application.proposal.TrainingProposalService;
import com.smalaca.trainingmanagement.domain.program.TrainingProgram;
import com.smalaca.trainingmanagement.domain.proposal.TrainingProposal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class TrainingProposalController {
    private final TrainingProposalService trainingProposalService;

    @GetMapping
    public ResponseEntity<List<TrainingProposal>> getAllProposals() {
        return ResponseEntity.ok(trainingProposalService.getAllProposals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingProposal> getProposal(@PathVariable UUID id) {
        return ResponseEntity.ok(trainingProposalService.getProposal(id));
    }

    @PostMapping
    public ResponseEntity<TrainingProposal> createProposal(@RequestBody CreateProposalRequest request) {
        TrainingProposal proposal = trainingProposalService.createProposal(
                request.title(),
                request.description(),
                request.category(),
                request.level(),
                request.trainer(),
                request.durationInDays()
        );
        return new ResponseEntity<>(proposal, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingProposal> updateProposal(
            @PathVariable UUID id,
            @RequestBody UpdateProposalRequest request) {
        TrainingProposal proposal = trainingProposalService.updateProposal(
                id,
                request.title(),
                request.description(),
                request.category(),
                request.level(),
                request.trainer(),
                request.durationInDays()
        );
        return ResponseEntity.ok(proposal);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<TrainingProgram> acceptProposal(@PathVariable UUID id) {
        TrainingProgram program = trainingProposalService.acceptProposal(id);
        return new ResponseEntity<>(program, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectProposal(@PathVariable UUID id) {
        trainingProposalService.rejectProposal(id);
        return ResponseEntity.noContent().build();
    }
}