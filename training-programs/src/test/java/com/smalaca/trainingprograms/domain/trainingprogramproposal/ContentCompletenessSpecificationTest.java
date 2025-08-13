package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class ContentCompletenessSpecificationTest {
    private final ContentCompletenessSpecification specification = new ContentCompletenessSpecification();
    
    @Test
    void shouldBeSatisfiedWhenAllContentMeetsMinimumRequirements() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Valid Training Program Name",
            generateText(100), // Exactly minimum description length
            generateText(200), // Exactly minimum agenda length
            generateText(300), // Exactly minimum plan length
            List.of(randomUUID(), randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldBeSatisfiedWhenAllContentExceedsMinimumRequirements() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Comprehensive Advanced Java Programming Training Program",
            generateText(500), // Well above minimum description length
            generateText(1000), // Well above minimum agenda length
            generateText(1500), // Well above minimum plan length
            List.of(randomUUID(), randomUUID(), randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenNameIsTooShort() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Java", // Name too short (less than 5 characters)
            generateText(100),
            generateText(200),
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenNameIsNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            null, // Name is null
            generateText(100),
            generateText(200),
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenNameIsEmpty() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "", // Name is empty
            generateText(100),
            generateText(200),
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenNameIsOnlyWhitespace() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "   ", // Name is only whitespace
            generateText(100),
            generateText(200),
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenDescriptionIsTooShort() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(99), // Description too short (less than 100 characters)
            generateText(200),
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenDescriptionIsNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            null, // Description is null
            generateText(200),
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenAgendaIsTooShort() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(100),
            generateText(199), // Agenda too short (less than 200 characters)
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenAgendaIsNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(100),
            null, // Agenda is null
            generateText(300),
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenPlanIsTooShort() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(100),
            generateText(200),
            generateText(299), // Plan too short (less than 300 characters)
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenPlanIsNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(100),
            generateText(200),
            null, // Plan is null
            List.of(randomUUID())
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenCategoriesAreEmpty() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(100),
            generateText(200),
            generateText(300),
            emptyList() // Categories are empty
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenCategoriesAreNull() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(100),
            generateText(200),
            generateText(300),
            null // Categories are null
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldBeSatisfiedWithSingleCategory() {
        TrainingProgramContent validContent = new TrainingProgramContent(
            "Valid Training Name",
            generateText(100),
            generateText(200),
            generateText(300),
            List.of(randomUUID()) // Single category
        );
        
        boolean result = specification.isSatisfiedBy(validContent);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldNotBeSatisfiedWhenMultipleRequirementsAreMissing() {
        TrainingProgramContent invalidContent = new TrainingProgramContent(
            "Bad", // Name too short
            generateText(50), // Description too short
            generateText(100), // Agenda too short
            generateText(200), // Plan too short
            emptyList() // No categories
        );
        
        boolean result = specification.isSatisfiedBy(invalidContent);
        
        assertThat(result).isFalse();
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