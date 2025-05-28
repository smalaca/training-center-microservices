package com.smalaca.discountmanagement.api;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.discountmanagement.api.DiscountCodeAlreadyUsedEventAssertion.assertThatDiscountCodeAlreadyUsedEvent;
import static com.smalaca.discountmanagement.api.DiscountCodeReturnedEventAssertion.assertThatDiscountCodeReturnedEvent;
import static com.smalaca.discountmanagement.api.DiscountCodeUsedEventAssertion.assertThatDiscountCodeUsedEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

class DiscountCommandProcessorTest {
    private static final Faker FAKER = new Faker();
    private static final String DISCOUNT_CODE_USED_TOPIC = "discount-code-used-topic";
    private static final String DISCOUNT_CODE_ALREADY_USED_TOPIC = "discount-code-already-used-topic";
    private static final String DISCOUNT_CODE_RETURNED_TOPIC = "discount-code-returned-topic";
    private static final BigDecimal ORIGINAL_PRICE = new BigDecimal("200.00");
    private static final BigDecimal NEW_PRICE = new BigDecimal("180.00");

    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final DiscountCommandProcessor processor = new DiscountCommandProcessor(kafkaTemplate, DISCOUNT_CODE_USED_TOPIC, DISCOUNT_CODE_ALREADY_USED_TOPIC, DISCOUNT_CODE_RETURNED_TOPIC);

    @Test
    void shouldPublishDiscountCodeUsedEventForNewDiscountCode() {
        UseDiscountCodeCommand command = useDiscountCodeCommand();

        processor.process(command);

        thenDiscountCodeUsedEventPublished()
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasParticipantId(command.participantId())
                .hasTrainingId(command.trainingId())
                .hasDiscountCode(command.discountCode())
                .hasOriginalPrice(command.priceAmount())
                .hasNewPrice(NEW_PRICE)
                .hasPriceCurrency(command.priceCurrencyCode());
    }

    private DiscountCodeUsedEventAssertion thenDiscountCodeUsedEventPublished() {
        return assertThatDiscountCodeUsedEvent(publishedDiscountCodeUsedEvent());
    }

    @Test
    void shouldPublishDiscountCodeAlreadyUsedEventForExistingDiscountCode() {
        String discountCode = randomDiscountCode();
        UseDiscountCodeCommand firstCommand = useDiscountCodeCommand(discountCode);
        processor.process(firstCommand);
        UseDiscountCodeCommand secondCommand = useDiscountCodeCommand(discountCode);

        processor.process(secondCommand);

        assertThatDiscountCodeAlreadyUsedEvent(publishedDiscountCodeAlreadyUsedEvent())
                .isNextAfter(secondCommand.commandId())
                .hasOfferId(secondCommand.offerId())
                .hasParticipantId(secondCommand.participantId())
                .hasTrainingId(secondCommand.trainingId())
                .hasDiscountCode(discountCode);
    }


    @ParameterizedTest
    @CsvSource({
            "100.00,90.00",
            "200.00,180.00",
            "50.00,45.00",
            "123.45,111.105",
            "10.00,9.00"
    })
    void shouldCalculateDiscountCorrectly(BigDecimal originalPrice, BigDecimal expectedNewPrice) {
        UseDiscountCodeCommand command = useDiscountCodeCommand(originalPrice);

        processor.process(command);

        DiscountCodeUsedEvent actual = publishedDiscountCodeUsedEvent();
        assertThat(actual.originalPrice()).isEqualByComparingTo(originalPrice);
        assertThat(actual.newPrice()).isEqualByComparingTo(expectedNewPrice);
    }

    private DiscountCodeAlreadyUsedEvent publishedDiscountCodeAlreadyUsedEvent() {
        ArgumentCaptor<DiscountCodeAlreadyUsedEvent> captor = ArgumentCaptor.forClass(DiscountCodeAlreadyUsedEvent.class);
        then(kafkaTemplate).should().send(eq(DISCOUNT_CODE_ALREADY_USED_TOPIC), captor.capture());
        return captor.getValue();
    }

    private DiscountCodeUsedEvent publishedDiscountCodeUsedEvent() {
        ArgumentCaptor<DiscountCodeUsedEvent> captor = ArgumentCaptor.forClass(DiscountCodeUsedEvent.class);
        then(kafkaTemplate).should().send(eq(DISCOUNT_CODE_USED_TOPIC), captor.capture());
        return captor.getValue();
    }

    @Test
    void shouldPublishDiscountCodeReturnedEventWhenDiscountCodeIsUsed() {
        ReturnDiscountCodeCommand command = returnDiscountCodeCommand();

        processor.process(command);

        assertThatDiscountCodeReturnedEvent(publishedDiscountCodeReturnedEvent())
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasParticipantId(command.participantId())
                .hasDiscountCode(command.discountCode());
    }

    private UseDiscountCodeCommand useDiscountCodeCommand() {
        return useDiscountCodeCommand(ORIGINAL_PRICE, randomDiscountCode());
    }

    private DiscountCodeReturnedEvent publishedDiscountCodeReturnedEvent() {
        ArgumentCaptor<DiscountCodeReturnedEvent> captor = ArgumentCaptor.forClass(DiscountCodeReturnedEvent.class);
        then(kafkaTemplate).should().send(eq(DISCOUNT_CODE_RETURNED_TOPIC), captor.capture());
        return captor.getValue();
    }

    private UseDiscountCodeCommand useDiscountCodeCommand(String discountCode) {
        return useDiscountCodeCommand(ORIGINAL_PRICE, discountCode);
    }

    private UseDiscountCodeCommand useDiscountCodeCommand(BigDecimal originalPrice) {
        return useDiscountCodeCommand(originalPrice, randomDiscountCode());
    }

    private UseDiscountCodeCommand useDiscountCodeCommand(BigDecimal originalPrice, String discountCode) {
        return new UseDiscountCodeCommand(
                randomCommandId(),
                randomId(),
                randomId(),
                randomId(),
                originalPrice,
                FAKER.currency().code(),
                discountCode
        );
    }

    private String randomDiscountCode() {
        return FAKER.commerce().promotionCode();
    }

    private ReturnDiscountCodeCommand returnDiscountCodeCommand() {
        return new ReturnDiscountCodeCommand(
                randomCommandId(),
                randomId(),
                randomId(),
                randomDiscountCode()
        );
    }

    private CommandId randomCommandId() {
        return new CommandId(randomId(), randomId(), randomId(), now());
    }

    private UUID randomId() {
        return UUID.randomUUID();
    }
}
