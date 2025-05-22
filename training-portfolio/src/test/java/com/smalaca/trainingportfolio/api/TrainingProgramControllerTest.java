package com.smalaca.trainingportfolio.api;

import com.smalaca.trainingportfolio.readmodel.TrainingProgramReadModel;
import com.smalaca.trainingportfolio.readmodel.TrainingProgramRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingProgramControllerTest {

    @Mock
    private TrainingProgramRepository trainingProgramRepository;

    @InjectMocks
    private TrainingProgramController controller;

    @Test
    void shouldGetTrainingProgramById() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProgramReadModel readModel = createReadModel(id, "Java Programming");
        given(trainingProgramRepository.findById(id)).willReturn(Optional.of(readModel));

        // when
        ResponseEntity<TrainingProgramReadModel> response = controller.getTrainingProgram(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(readModel);
    }

    @Test
    void shouldReturnNotFoundWhenTrainingProgramNotFound() {
        // given
        UUID id = UUID.randomUUID();
        given(trainingProgramRepository.findById(id)).willReturn(Optional.empty());

        // when
        ResponseEntity<TrainingProgramReadModel> response = controller.getTrainingProgram(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldGetAllTrainingProgramsWithDefaultPagination() {
        // given
        List<TrainingProgramReadModel> programs = List.of(
                createReadModel(UUID.randomUUID(), "Java Programming"),
                createReadModel(UUID.randomUUID(), "Python Programming")
        );
        Page<TrainingProgramReadModel> page = new PageImpl<>(programs);
        
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        given(trainingProgramRepository.findAll(expectedPageable)).willReturn(page);

        // when
        ResponseEntity<Page<TrainingProgramReadModel>> response = controller.getTrainingPrograms(
                null, null, null, null, 0, 10, "title", "asc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
        verify(trainingProgramRepository).findAll(expectedPageable);
    }

    @Test
    void shouldGetTrainingProgramsWithFilters() {
        // given
        String category = "Programming";
        String level = "Beginner";
        String trainer = "John Doe";
        Boolean active = true;
        
        List<TrainingProgramReadModel> programs = List.of(
                createReadModel(UUID.randomUUID(), "Java Programming")
        );
        Page<TrainingProgramReadModel> page = new PageImpl<>(programs);
        
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        given(trainingProgramRepository.findByFilters(
                eq(category), eq(level), eq(trainer), eq(active), any(Pageable.class)))
                .willReturn(page);

        // when
        ResponseEntity<Page<TrainingProgramReadModel>> response = controller.getTrainingPrograms(
                category, level, trainer, active, 0, 10, "title", "asc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
        verify(trainingProgramRepository).findByFilters(
                eq(category), eq(level), eq(trainer), eq(active), eq(expectedPageable));
    }

    @Test
    void shouldGetTrainingProgramsWithCustomPaginationAndSorting() {
        // given
        List<TrainingProgramReadModel> programs = List.of(
                createReadModel(UUID.randomUUID(), "Java Programming"),
                createReadModel(UUID.randomUUID(), "Python Programming")
        );
        Page<TrainingProgramReadModel> page = new PageImpl<>(programs);
        
        int pageNumber = 2;
        int pageSize = 5;
        String sortBy = "category";
        String sortDirection = "desc";
        
        Pageable expectedPageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortBy));
        given(trainingProgramRepository.findAll(expectedPageable)).willReturn(page);

        // when
        ResponseEntity<Page<TrainingProgramReadModel>> response = controller.getTrainingPrograms(
                null, null, null, null, pageNumber, pageSize, sortBy, sortDirection);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
        verify(trainingProgramRepository).findAll(expectedPageable);
    }

    private TrainingProgramReadModel createReadModel(UUID id, String title) {
        TrainingProgramReadModel readModel = new TrainingProgramReadModel(
                id,
                title,
                "Description of " + title,
                "Programming",
                "Beginner",
                "John Doe",
                5
        );
        
        // Set timestamps to avoid null values
        readModel.setCreatedAt(LocalDateTime.now());
        readModel.setUpdatedAt(LocalDateTime.now());
        
        return readModel;
    }
}