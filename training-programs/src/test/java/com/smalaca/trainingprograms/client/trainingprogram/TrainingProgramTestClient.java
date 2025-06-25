package com.smalaca.trainingprograms.client.trainingprogram;

import com.smalaca.trainingprograms.client.trainingprogram.trainingprogramproposal.TrainingProgramProposalEndpoints;
import org.springframework.test.web.servlet.MockMvc;

public class TrainingProgramTestClient {
    private final MockMvc mockMvc;

    TrainingProgramTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public TrainingProgramProposalEndpoints trainingProgramProposals() {
        return new TrainingProgramProposalEndpoints(mockMvc);
    }
}