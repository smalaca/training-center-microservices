package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainingProgramProposalReviewSpecificationTest {
    private final TrainingProgramProposalReviewSpecification specification = new TrainingProgramProposalReviewSpecificationFactory().trainingProgramProposalReviewSpecification();

    @Test
    void shouldSatisfySpecificationWhenContentMeetsAllRequirements() {
        TrainingProgramContent content = validTrainingProgramContent();

        boolean actual = specification.isSatisfiedBy(content);

        assertTrue(actual);
    }

    @ParameterizedTest
    @MethodSource("plansWithLogicalFlow")
    void shouldSatisfySpecificationWhenPlanHasLogicalFlow(String plan) {
        TrainingProgramContent content = trainingProgramContentBuilder()
                .plan(plan)
                .build();

        boolean actual = specification.isSatisfiedBy(content);

        assertTrue(actual);
    }

    public static Stream<String> plansWithLogicalFlow() {
        return Stream.of(
                "Step 1: Foundation Building\n" +
                "Step 1A Review core Java concepts and introduce advanced topics\n" +
                "Step 1B Hands-on exercises with design patterns\n" +
                "Step 1C. Code review Steps and best practices discussion\n" +
                "\nStep 2: Advanced Topics\n" +
                "Step 2A: Deep dive into concurrency mechanisms\n" +
                "Step 2B: Practical exercises with threading\n" +
                "Step 2C: Performance analysis and optimization\n" +
                "\nStep 3: Real-world Application\n" +
                "Step 3A: Project work applying learned concepts\n" +
                "Step 3B: Code review and feedback\n" +
                "Step 3C: Final presentations and wrap-up",

                "Phase 1: Foundation Building\n" +
                "Phase 1A Review core Java concepts and introduce advanced topics\n" +
                "Phase 1B Hands-on exercises with design patterns\n" +
                "Phase 1C. Code review Phases and best practices discussion\n" +
                "\nPhase 2: Advanced Topics\n" +
                "Phase 2A: Deep dive into concurrency mechanisms\n" +
                "Phase 2B: Practical exercises with threading\n" +
                "Phase 2C: Performance analysis and optimization\n" +
                "\nPhase 3: Real-world Application\n" +
                "Phase 3A: Project work applying learned concepts\n" +
                "Phase 3B: Code review and feedback\n" +
                "Phase 3C: Final presentations and wrap-up",

                "Module 1: Foundation Building\n" +
                "Module 1A Review core Java concepts and introduce advanced topics\n" +
                "Module 1B Hands-on exercises with design patterns\n" +
                "Module 1C. Code review Modules and best practices discussion\n" +
                "\nModule 2: Advanced Topics\n" +
                "Module 2A: Deep dive into concurrency mechanisms\n" +
                "Module 2B: Practical exercises with threading\n" +
                "Module 2C: Performance analysis and optimization\n" +
                "\nModule 3: Real-world Application\n" +
                "Module 3A: Project work applying learned concepts\n" +
                "Module 3B: Code review and feedback\n" +
                "Module 3C: Final presentations and wrap-up",

                "Session 1: Foundation Building\n" +
                "Session 1A Review core Java concepts and introduce advanced topics\n" +
                "Session 1B Hands-on exercises with design patterns\n" +
                "Session 1C. Code review sessions and best practices discussion\n" +
                "\nSession 2: Advanced Topics\n" +
                "Session 2A: Deep dive into concurrency mechanisms\n" +
                "Session 2B: Practical exercises with threading\n" +
                "Session 2C: Performance analysis and optimization\n" +
                "\nSession 3: Real-world Application\n" +
                "Session 3A: Project work applying learned concepts\n" +
                "Session 3B: Code review and feedback\n" +
                "Session 3C: Final presentations and wrap-up"
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotSatisfySpecificationWhenHasNoName(String name) {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .name(name)
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenNameIsTooShort() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .name("Java")
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotSatisfySpecificationWhenHasNoDescription(String description) {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .description(description)
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenDescriptionIsTooShort() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .description("Short description")
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotSatisfySpecificationWhenHasNoAgenda(String agenda) {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .agenda(agenda)
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenAgendaIsTooShort() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .agenda("Short agenda")
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotSatisfySpecificationWhenHasNoPlan(String plan) {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .plan(plan)
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenPlanIsTooShort() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .plan("Short plan")
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenCategoriesIsNull() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .categoriesIds(null)
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenCategoriesIsEmpty() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .categoriesIds(emptyList())
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenAgendaHasNoProperHeadings() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .agenda(agendaWithoutHeadings())
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenPlanHasNoLogicalFlow() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .plan(planWithoutLogicalFlow())
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenDescriptionHasNoLearningObjectives() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .description(descriptionWithoutLearningObjectives())
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenContentHasExcessiveRepetition() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .description(descriptionWithExcessiveRepetition())
            .build();
        
        boolean actual = specification.isSatisfiedBy(content);
        
        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenAgendaHasExcessiveRepetition() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .agenda(agendaWithExcessiveRepetition())
            .build();

        boolean actual = specification.isSatisfiedBy(content);

        assertFalse(actual);
    }

    @Test
    void shouldNotSatisfySpecificationWhenPlanHasExcessiveRepetition() {
        TrainingProgramContent content = trainingProgramContentBuilder()
            .plan(planWithExcessiveRepetition())
            .build();

        boolean actual = specification.isSatisfiedBy(content);

        assertFalse(actual);
    }

    private TrainingProgramContent validTrainingProgramContent() {
        return trainingProgramContentBuilder().build();
    }

    private TrainingProgramContentBuilder trainingProgramContentBuilder() {
        return new TrainingProgramContentBuilder()
            .name("Advanced Java Programming Course")
            .description(validDescription())
            .agenda(validAgenda())
            .plan(validPlan())
            .categoriesIds(List.of(randomUUID(), randomUUID()));
    }

    private String validDescription() {
        return "This comprehensive course will teach you advanced Java programming concepts and techniques. " +
               "You will learn about design patterns, concurrency, performance optimization, and modern Java features. " +
               "Students will master advanced object-oriented programming principles and understand how to apply them in real-world scenarios.";
    }

    private String validAgenda() {
        return "# Day 1: Fundamentals\n" +
               "* Advanced OOP concepts\n" +
               "* Design patterns overview\n" +
               "* SOLID principles in practice\n" +
               "\n# Day 2: Concurrency\n" +
               "* Threading and synchronization\n" +
               "* Concurrent collections\n" +
               "* CompletableFuture and reactive programming\n" +
               "\n# Day 3: Performance\n" +
               "* JVM tuning and garbage collection\n" +
               "* Profiling and monitoring tools\n" +
               "* Memory management best practices";
    }

    private String validPlan() {
        return "Phase 1: Foundation Building\n" +
               "1. Review core Java concepts and introduce advanced topics\n" +
               "2. Hands-on exercises with design patterns\n" +
               "3. Code review sessions and best practices discussion\n" +
               "\nPhase 2: Advanced Topics\n" +
               "Step 1: Deep dive into concurrency mechanisms\n" +
               "Step 2: Practical exercises with threading\n" +
               "Step 3: Performance analysis and optimization\n" +
               "\nModule 3: Real-world Application\n" +
               "Session 1: Project work applying learned concepts\n" +
               "Session 2: Code review and feedback\n" +
               "Session 3: Final presentations and wrap-up";
    }

    private String agendaWithoutHeadings() {
        return "This is a long agenda without any proper headings or structure. " +
               "It just contains plain text that goes on and on without any formatting. " +
               "There are no bullet points, no numbered lists, no hash symbols, and no proper organization. " +
               "This content would fail the proper headings check because it lacks any structural elements.";
    }

    private String planWithoutLogicalFlow() {
        return "This is a plan that lacks any logical flow indicators. " +
               "It contains a lot of text but has no numbered steps or phases. " +
               "There are no modules or sessions mentioned anywhere. " +
               "The content is just a wall of text without any organizational structure. " +
               "This would fail the logical flow check because it contains none of the required keywords. " +
               "The plan should have clear progression but this one does not. " +
               "It is just random thoughts put together without proper planning. " +
               "There is no clear beginning, middle, or end to the educational process.";
    }

    private String descriptionWithoutLearningObjectives() {
        return "This is a description that contains no educational keywords related to objectives. " +
               "It talks about various topics and concepts but never mentions what students will gain. " +
               "The content discusses technical details and features but avoids instructional language. " +
               "There are no references to acquiring knowledge or developing skills. " +
               "The text focuses on describing things rather than educational outcomes. " +
               "This content deliberately avoids specific objective-related terminology to test the specification.";
    }

    private String descriptionWithExcessiveRepetition() {
        return "Java Java Java Java Java Java Java Java Java Java " +
               "programming programming programming programming programming programming programming programming programming programming " +
               "course course course course course course course course course course " +
               "will learn basic concepts and techniques for beginners. " +
               "The focus is on Java programming fundamentals and course structure.";
    }

    private String agendaWithExcessiveRepetition() {
        return """
                # Introduction Introduction Introduction Introduction Introduction
                1. Introduction Introduction Introduction Introduction Introduction
                2. Fundamentals Fundamentals Fundamentals Fundamentals Fundamentals
                3. Java Java Java Java Java Java Java Java Java Java 
                * Java Java Java Java Java Java Java Java Java Java 
                - Programming Programming Programming Programming Programming Programming Programming
                """;
    }

    private String planWithExcessiveRepetition() {
        return """
                1. Module Module Module Module Module introduction to basics
                2. Module Module Module Module Module advanced concepts
                3. Module Module Module Module Module hands-on sessions
                Step 1: Module Module Module Module review of fundamentals
                Step 2: Module Module Module Module interactive exercises
                Phase 1: Module Module Module Module advanced topics
                Phase 2: Module Module Module Module wrap-up and summary
                """;
    }

    private static class TrainingProgramContentBuilder {

        private String name;
        private String description;
        private String agenda;
        private String plan;
        private List<UUID> categoriesIds;

        public TrainingProgramContentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TrainingProgramContentBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TrainingProgramContentBuilder agenda(String agenda) {
            this.agenda = agenda;
            return this;
        }

        public TrainingProgramContentBuilder plan(String plan) {
            this.plan = plan;
            return this;
        }

        public TrainingProgramContentBuilder categoriesIds(List<UUID> categoriesIds) {
            this.categoriesIds = categoriesIds;
            return this;
        }

        public TrainingProgramContent build() {
            return new TrainingProgramContent(name, description, agenda, plan, categoriesIds);
        }
    }
}