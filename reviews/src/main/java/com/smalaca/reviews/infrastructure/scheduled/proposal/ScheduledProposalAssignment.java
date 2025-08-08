package com.smalaca.reviews.infrastructure.scheduled.proposal;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledProposalAssignment {
    private final ProposalApplicationService applicationService;

    ScheduledProposalAssignment(ProposalApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @DrivingAdapter
    @Scheduled(fixedRateString = "${scheduled.proposal.assignment.rate}")
    void assignProposals() {
        applicationService.assignProposalsForScheduledAssignment();
    }
}