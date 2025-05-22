package com.smalaca.trainingportfolio.readmodel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgramReadModel, UUID> {
    
    Page<TrainingProgramReadModel> findByActive(boolean active, Pageable pageable);
    
    Page<TrainingProgramReadModel> findByCategory(String category, Pageable pageable);
    
    Page<TrainingProgramReadModel> findByLevel(String level, Pageable pageable);
    
    Page<TrainingProgramReadModel> findByTrainer(String trainer, Pageable pageable);
    
    @Query("SELECT t FROM TrainingProgramReadModel t WHERE " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:level IS NULL OR t.level = :level) AND " +
           "(:trainer IS NULL OR t.trainer = :trainer) AND " +
           "(:active IS NULL OR t.active = :active)")
    Page<TrainingProgramReadModel> findByFilters(
            @Param("category") String category,
            @Param("level") String level,
            @Param("trainer") String trainer,
            @Param("active") Boolean active,
            Pageable pageable);
}