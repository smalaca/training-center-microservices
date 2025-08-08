package com.smalaca.reviews.infrastructure.scheduled.proposal;

import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ScheduledProposalAssignmentTest {
    private final ProposalApplicationService applicationService = mock(ProposalApplicationService.class);
    private final ScheduledProposalAssignment scheduledAssignment = new ScheduledProposalAssignment(applicationService);

    @Test
    void shouldCallApplicationServiceWhenAssigningProposals() {
        scheduledAssignment.assignProposals();

        verify(applicationService).assignProposalsForScheduledAssignment();
    }
}