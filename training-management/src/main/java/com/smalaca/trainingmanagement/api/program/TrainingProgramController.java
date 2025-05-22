package com.smalaca.trainingmanagement.api.program;

import com.smalaca.trainingmanagement.application.program.TrainingProgramService;
import com.smalaca.trainingmanagement.domain.program.TrainingProgram;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class TrainingProgramController {
    private final TrainingProgramService trainingProgramService;

    @GetMapping
    public ResponseEntity<List<TrainingProgram>> getAllPrograms() {
        return ResponseEntity.ok(trainingProgramService.getAllPrograms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingProgram> getProgram(@PathVariable UUID id) {
        return ResponseEntity.ok(trainingProgramService.getProgram(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingProgram> updateProgram(
            @PathVariable UUID id,
            @RequestBody UpdateProgramRequest request) {
        TrainingProgram program = trainingProgramService.updateProgram(
                id,
                request.title(),
                request.description(),
                request.category(),
                request.level(),
                request.trainer(),
                request.durationInDays()
        );
        return ResponseEntity.ok(program);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdrawProgram(
            @PathVariable UUID id,
            @RequestBody WithdrawProgramRequest request) {
        trainingProgramService.withdrawProgram(id, request.reason());
        return ResponseEntity.noContent().build();
    }
}