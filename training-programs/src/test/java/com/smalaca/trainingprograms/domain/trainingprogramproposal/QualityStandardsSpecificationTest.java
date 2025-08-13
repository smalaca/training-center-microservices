package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class QualityStandardsSpecificationTest {
    private final QualityStandardsSpecification specification = new QualityStandardsSpecification();
    
    @Test
    void shouldBeSatisfiedWhenAllQualityStandardsAreMet() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Advanced Java Programming Training",
            "This comprehensive course will teach participants to learn advanced Java concepts and apply modern programming practices. Students will understand design patterns and master concurrent programming techniques.",
            "# Module 1: Core Concepts\n# Module 2: Advanced Features\n* Design Patterns\n* Concurrency\n1. Introduction\n2. Practical Examples",
            "Phase 1: Foundation building with theoretical concepts. Step 2: Hands-on practice sessions. Module 3: Real-world project development. Session 4: Code review and optimization techniques.",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithLearningObjectivesInDescription() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Participants will learn modern software development practices, understand design principles, and master testing techniques through hands-on experience.",
            "# Introduction\n* Overview\n1. Getting Started",
            "Step 1: Basic concepts\nPhase 2: Advanced topics\nModule 3: Practice",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithDifferentLearningObjectiveVerbs() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Students will analyze code quality, evaluate different approaches, demonstrate best practices, and apply learned concepts in real projects.",
            "# Module 1\n* Topic A\n1. Section One",
            "Step 1: Foundation\nPhase 2: Application\nModule 3: Assessment",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenDescriptionLacksLearningObjectives() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "This is a basic course about programming. It covers various topics and includes practical exercises with different examples and case studies.",
            "# Module 1\n* Topic A\n1. Section One",
            "Step 1: Foundation\nPhase 2: Application\nModule 3: Assessment",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenDescriptionIsNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            null, // No description
            "# Module 1\n* Topic A\n1. Section One",
            "Step 1: Foundation\nPhase 2: Application\nModule 3: Assessment",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldBeSatisfiedWithHashtagHeadings() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn advanced programming concepts and understand software architecture principles.",
            "# Introduction to Programming\n# Advanced Concepts\n# Practical Applications",
            "Step 1: Basic concepts\nPhase 2: Advanced topics",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithBulletPointHeadings() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Participants will master modern development techniques and apply best practices in real projects.",
            "* Core Programming Concepts\n* Advanced Design Patterns\n* Testing Strategies",
            "Step 1: Foundation building\nPhase 2: Practical application",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithNumberedHeadings() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Students will understand core principles and demonstrate practical skills through hands-on exercises.",
            "1. Introduction to Java\n2. Object-Oriented Programming\n3. Advanced Topics",
            "Step 1: Theoretical foundation\nPhase 2: Hands-on practice",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithDashHeadings() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Participants will learn essential programming skills and apply them in practical scenarios.",
            "- Basic Syntax and Structure\n- Advanced Programming Concepts\n- Project Development",
            "Step 1: Learning fundamentals\nPhase 2: Building projects",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenAgendaLacksProperHeadings() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn programming concepts and understand software development principles.",
            "Basic introduction to programming concepts followed by advanced topics and practical exercises",
            "Step 1: Introduction\nPhase 2: Practice\nModule 3: Projects",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenAgendaIsNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn programming and understand development practices.",
            null, // No agenda
            "Step 1: Introduction\nPhase 2: Practice\nModule 3: Projects",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenAgendaIsEmpty() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn programming and understand development practices.",
            "", // Empty agenda
            "Step 1: Introduction\nPhase 2: Practice\nModule 3: Projects",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldBeSatisfiedWithStepBasedLogicalFlow() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Participants will learn programming fundamentals and understand software architecture.",
            "# Module 1\n* Introduction\n1. Getting Started",
            "Step 1: Introduction to concepts\nStep 2: Practical exercises\nStep 3: Project development",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithPhaseBasedLogicalFlow() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Students will master development techniques and apply best practices.",
            "# Learning Path\n* Core Concepts\n1. Fundamentals",
            "Phase 1: Foundation building\nPhase 2: Skill development\nPhase 3: Advanced applications",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithModuleBasedLogicalFlow() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Participants will understand core principles and demonstrate practical skills.",
            "# Course Structure\n* Learning Objectives\n1. Overview",
            "Module 1: Basic concepts\nModule 2: Intermediate topics\nModule 3: Advanced applications",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithSessionBasedLogicalFlow() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn essential skills and apply them in practice.",
            "# Training Agenda\n* Key Topics\n1. Introduction",
            "Session 1: Fundamentals\nSession 2: Hands-on practice\nSession 3: Project work",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithNumberedLogicalFlow() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Participants will master programming concepts and understand development practices.",
            "# Course Overview\n* Main Topics\n1. Getting Started",
            "1. Introduction to programming\n2. Core concepts and principles\n3. Advanced topics and applications",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenPlanLacksLogicalFlow() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn programming and understand software development.",
            "# Course Content\n* Topics\n1. Overview",
            "Random topics covered in no particular order with various concepts and different approaches",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenPlanIsNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn programming and understand development practices.",
            "# Course Structure\n* Main Topics\n1. Introduction",
            null, // No plan
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenPlanIsEmpty() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn programming and understand development practices.",
            "# Course Structure\n* Main Topics\n1. Introduction",
            "", // Empty plan
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenWordCountIsBelowMinimum() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn basic concepts.", // Very short description
            "# Topic\n* Item", // Very short agenda
            "Step 1: Learn", // Very short plan
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldBeSatisfiedWhenWordCountMeetsMinimum() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn comprehensive programming concepts and understand software development principles through practical exercises and hands-on experience with modern development tools and techniques.",
            "# Module 1\n* Introduction to programming concepts\n1. Getting started with development environment setup and basic syntax",
            "Step 1: Foundation building with theoretical concepts and practical examples for better understanding of core principles",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithoutExcessiveRepetition() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn diverse programming concepts, understand various software patterns, master different development techniques, and apply multiple problem-solving approaches.",
            "# Module 1\n* Introduction\n1. Overview",
            "Step 1: Foundation\nPhase 2: Practice\nModule 3: Application",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldNotBeSatisfiedWithExcessiveRepetition() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Students will learn programming programming programming programming programming programming programming programming programming programming programming programming programming concepts.",
            "# Module programming programming programming programming programming programming programming programming programming programming programming programming programming",
            "Step programming programming programming programming programming programming programming programming programming programming programming programming programming programming",
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenMultipleQualityStandardsAreMissing() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Training Program",
            "Basic course description", // No learning objectives
            "Simple agenda content", // No proper headings
            "Basic plan content", // No logical flow indicators
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
}