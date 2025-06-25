package com.smalaca.trainingprograms.client.trainingprogram.trainingprogramproposal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingProgramProposalEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TrainingProgramProposalEndpoints(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public RestTrainingProgramProposalTestResponse propose(CreateTrainingProgramProposalCommand command) {
        try {
            String content = objectMapper.writeValueAsString(command);
            return performSafe(post("/trainingprogramproposal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RestTrainingProgramProposalTestResponse findAll() {
        return performSafe(get("/trainingprogramproposal"));
    }

    public RestTrainingProgramProposalTestResponse findById(UUID trainingProgramProposalId) {
        return performSafe(get("/trainingprogramproposal/" + trainingProgramProposalId));
    }

    private RestTrainingProgramProposalTestResponse performSafe(RequestBuilder request) {
        try {
            MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
            return new RestTrainingProgramProposalTestResponse(response, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}