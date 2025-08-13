package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramProposalReviewSpecificationTest {
    private final ContentCompletenessSpecification contentCompleteness = new ContentCompletenessSpecification();
    private final QualityStandardsSpecification qualityStandards = new QualityStandardsSpecification();
    private final TrainingProgramProposalReviewSpecification combinedSpecification = contentCompleteness.and(qualityStandards);
    
    @Test
    void shouldBeSatisfiedWhenBothSpecificationsAreMet() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Advanced Java Programming Training Course",
            generateText(150) + " Students will learn advanced Java concepts, understand design patterns, master concurrent programming, and apply best practices in real-world projects.",
            "# Module 1: Core Java Concepts\n# Module 2: Advanced Features\n* Object-Oriented Programming\n* Design Patterns\n1. Introduction to Java\n2. Advanced Topics",
            generateText(350) + " Phase 1: Foundation building with core concepts. Step 2: Hands-on practice with real examples. Module 3: Advanced project development. Session 4: Code review and optimization.",
            List.of(randomUUID(), randomUUID())
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenContentCompletenessFailsButQualityStandardsPasses() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Java", // Name too short - fails content completeness
            generateText(150) + " Students will learn advanced programming concepts and understand software development principles through practical experience.",
            "# Module 1: Programming Basics\n# Module 2: Advanced Concepts\n* Core Topics\n1. Introduction",
            generateText(350) + " Step 1: Basic concepts and fundamentals. Phase 2: Advanced applications and practice. Module 3: Real-world projects.",
            List.of(randomUUID())
        );
        
        // Verify individual specifications
        assertThat(contentCompleteness.isSatisfiedBy(invalidContent)).isFalse();
        assertThat(qualityStandards.isSatisfiedBy(invalidContent)).isTrue();
        
        // Combined should fail
        boolean result = combinedSpecification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenQualityStandardsFailsButContentCompletenessPasses() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Comprehensive Java Programming Training Course",
            generateText(150) + " This course covers programming concepts and includes practical exercises.", // No learning objectives - fails quality standards
            generateText(250), // No proper headings - fails quality standards
            generateText(350) + " Various topics and concepts covered.", // No logical flow - fails quality standards
            List.of(randomUUID(), randomUUID())
        );
        
        // Verify individual specifications
        assertThat(contentCompleteness.isSatisfiedBy(invalidContent)).isTrue();
        assertThat(qualityStandards.isSatisfiedBy(invalidContent)).isFalse();
        
        // Combined should fail
        boolean result = combinedSpecification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenBothSpecificationsFail() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Bad", // Name too short - fails content completeness
            "Short", // Description too short - fails content completeness, no learning objectives - fails quality
            "Brief", // Agenda too short - fails content completeness, no headings - fails quality
            "Basic", // Plan too short - fails content completeness, no flow - fails quality
            emptyList() // No categories - fails content completeness
        );
        
        // Verify individual specifications
        assertThat(contentCompleteness.isSatisfiedBy(invalidContent)).isFalse();
        assertThat(qualityStandards.isSatisfiedBy(invalidContent)).isFalse();
        
        // Combined should fail
        boolean result = combinedSpecification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldWorkWithFactoryCreatedSpecification() {
        TrainingProgramProposalReviewSpecificationFactory factory = new TrainingProgramProposalReviewSpecificationFactory();
        TrainingProgramProposalReviewSpecification factorySpec = factory.reviewSpecification();
        
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Advanced Software Development Training",
            generateText(120) + " Participants will learn modern development practices, understand architectural patterns, and master testing techniques.",
            "# Module 1: Fundamentals\n# Module 2: Advanced Topics\n* Design Patterns\n* Testing Strategies\n1. Introduction\n2. Practical Applications",
            generateText(320) + " Step 1: Foundation concepts and theory. Phase 2: Hands-on practice and implementation. Module 3: Advanced project work. Session 4: Review and optimization.",
            List.of(randomUUID())
        );
        
        boolean result = factorySpec.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldHandleBoundaryConditionsForContentLength() {
        TrainingProgramContent boundaryContent = new TrainingProgramContent(
            "Valid Training Program Name", // Exactly 5+ characters
            generateText(100) + " Students will learn core concepts and understand practical applications.", // Exactly 100+ chars + learning objectives
            "# Module One\n* Topic A\n1. Section" + generateText(150), // Exactly 200+ chars + proper headings
            "Step 1: Basic foundation" + generateText(270), // Exactly 300+ chars + logical flow
            List.of(randomUUID()) // Exactly 1 category
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(boundaryContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldFailWhenJustBelowBoundaryConditions() {
        TrainingProgramContent belowBoundaryContent = new TrainingProgramContent(
            "Test", // Exactly 4 characters - just below minimum
            generateText(99) + " Students will learn and understand concepts.", // Exactly 99 chars - just below minimum
            "# Topic" + generateText(190), // Exactly 199 chars - just below minimum
            "Step 1:" + generateText(290), // Exactly 299 chars - just below minimum
            List.of(randomUUID())
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(belowBoundaryContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldHandleNullValuesGracefully() {
        TrainingProgramContent nullContent = new TrainingProgramContent(
            null, // Null name
            null, // Null description
            null, // Null agenda
            null, // Null plan
            null  // Null categories
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(nullContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldHandleEmptyStringsGracefully() {
        TrainingProgramContent emptyContent = new TrainingProgramContent(
            "", // Empty name
            "", // Empty description
            "", // Empty agenda
            "", // Empty plan
            emptyList() // Empty categories
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(emptyContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldHandleWhitespaceOnlyContent() {
        TrainingProgramContent whitespaceContent = new TrainingProgramContent(
            "   ", // Whitespace-only name
            "   ", // Whitespace-only description
            "   ", // Whitespace-only agenda
            "   ", // Whitespace-only plan
            List.of(randomUUID())
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(whitespaceContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldBeSatisfiedWithMinimalValidContent() {
        TrainingProgramContent minimalValidContent = new TrainingProgramContent(
            "Basic Training Course Name",
            generateText(100) + " Students will learn programming fundamentals and understand development principles through practice.",
            "# Module 1\n* Introduction\n1. Getting Started" + generateText(150),
            "Step 1: Foundation concepts" + generateText(260),
            List.of(randomUUID())
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(minimalValidContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWithRichValidContent() {
        TrainingProgramContent richContent = new TrainingProgramContent(
            "Comprehensive Advanced Software Engineering and Architecture Training Program",
            generateText(500) + " Participants will learn advanced software engineering principles, understand complex architectural patterns, master modern development methodologies, apply best practices in enterprise environments, demonstrate proficiency in system design, analyze performance characteristics, and evaluate different technological solutions.",
            "# Module 1: Software Engineering Fundamentals\n# Module 2: Architectural Patterns and Design\n# Module 3: Advanced Development Practices\n# Module 4: Enterprise Integration\n* Design Patterns and Principles\n* Microservices Architecture\n* Testing Strategies and Quality Assurance\n* Performance Optimization Techniques\n1. Introduction to Software Engineering\n2. Object-Oriented Design and Programming\n3. Functional Programming Concepts\n4. Concurrent and Parallel Programming" + generateText(800),
            "Phase 1: Foundation building with comprehensive theoretical background and fundamental concepts. Step 2: Intermediate skill development through guided hands-on exercises and practical implementations. Module 3: Advanced topic exploration with real-world case studies and industry best practices. Session 4: Capstone project development with mentor guidance and peer collaboration. Phase 5: Final presentations and comprehensive code reviews with detailed feedback." + generateText(1000),
            List.of(randomUUID(), randomUUID(), randomUUID(), randomUUID(), randomUUID())
        );
        
        boolean result = combinedSpecification.isSatisfiedBy(richContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldTestSpecificationComposition() {
        // Test that the and() method creates a proper composition
        TrainingProgramContent testContent = new TrainingProgramContent(
            "Test Training Program",
            generateText(100) + " Students will learn and understand concepts through practice.",
            "# Module 1\n* Topic A\n1. Section One" + generateText(150),
            "Step 1: Foundation" + generateText(270),
            List.of(randomUUID())
        );
        
        // Test individual specifications
        boolean contentResult = contentCompleteness.isSatisfiedBy(testContent);
        boolean qualityResult = qualityStandards.isSatisfiedBy(testContent);
        boolean combinedResult = combinedSpecification.isSatisfiedBy(testContent);
        
        assertThat(contentResult).isTrue();
        assertThat(qualityResult).isTrue();
        assertThat(combinedResult).isTrue();
        assertThat(combinedResult).isEqualTo(contentResult && qualityResult);
    }
    
    private String generateText(int length) {
        StringBuilder text = new StringBuilder();
        String word = "sample ";
        while (text.length() < length) {
            text.append(word);
        }
        return text.substring(0, length);
    }
}