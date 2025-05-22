package com.smalaca.trainingportfolio.api;

import com.smalaca.trainingportfolio.readmodel.TrainingProgramReadModel;
import com.smalaca.trainingportfolio.readmodel.TrainingProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/training-programs")
@RequiredArgsConstructor
public class TrainingProgramController {
    
    private final TrainingProgramRepository trainingProgramRepository;
    
    @GetMapping("/{id}")
    public ResponseEntity<TrainingProgramReadModel> getTrainingProgram(@PathVariable UUID id) {
        return trainingProgramRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<TrainingProgramReadModel>> getTrainingPrograms(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String trainer,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<TrainingProgramReadModel> programs;
        
        if (category != null || level != null || trainer != null || active != null) {
            programs = trainingProgramRepository.findByFilters(category, level, trainer, active, pageable);
        } else {
            programs = trainingProgramRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(programs);
    }
}