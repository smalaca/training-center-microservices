package com.smalaca.reviews.infrastructure.scheduled.proposal;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import com.smalaca.reviews.query.proposal.ProposalQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledProposalAssignment {
    private final ProposalApplicationService applicationService;
    private final ProposalQueryService queryService;

    ScheduledProposalAssignment(ProposalApplicationService applicationService, ProposalQueryService queryService) {
        this.applicationService = applicationService;
        this.queryService = queryService;
    }

    @DrivingAdapter
    @Scheduled(fixedRateString = "${scheduled.proposal.assignment.rate}")
    void assignProposals() {
        queryService.findAllToAssign().forEach(proposalView -> applicationService.assign(proposalView.getProposalId()));
    }
}