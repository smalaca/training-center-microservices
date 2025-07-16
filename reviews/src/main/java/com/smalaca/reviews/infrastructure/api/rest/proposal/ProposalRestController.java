package com.smalaca.reviews.infrastructure.api.rest.proposal;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import com.smalaca.reviews.infrastructure.repository.jpa.proposal.ProposalDoesNotExistException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("proposal")
public class ProposalRestController {
    private final ProposalApplicationService applicationService;

    ProposalRestController(ProposalApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PutMapping("/accept")
    @DrivingAdapter
    public ResponseEntity<Void> accept(@RequestBody CompleteReviewCommand command) {
        try {
            applicationService.approve(command.proposalId(), command.reviewerId());
            return ResponseEntity.ok().build();
        } catch (ProposalDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reject")
    @DrivingAdapter
    public ResponseEntity<Void> reject(@RequestBody CompleteReviewCommand command) {
        try {
            applicationService.reject(command.proposalId(), command.reviewerId());
            return ResponseEntity.ok().build();
        } catch (ProposalDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
