package com.smalaca.reviews.infrastructure.api.rest.proposal;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import com.smalaca.reviews.infrastructure.repository.jpa.proposal.ProposalDoesNotExistException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("proposal")
public class ProposalRestController {
    private final ProposalApplicationService applicationService;

    ProposalRestController(ProposalApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PutMapping("/accept/{proposalId}/{reviewerId}")
    @DrivingAdapter
    public ResponseEntity<Void> accept(@PathVariable UUID proposalId, @PathVariable UUID reviewerId) {
        try {
            applicationService.approve(proposalId, reviewerId);
            return ResponseEntity.ok().build();
        } catch (ProposalDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reject/{proposalId}/{reviewerId}")
    @DrivingAdapter
    public ResponseEntity<Void> reject(@PathVariable UUID proposalId, @PathVariable UUID reviewerId) {
        try {
            applicationService.reject(proposalId, reviewerId);
            return ResponseEntity.ok().build();
        } catch (ProposalDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}