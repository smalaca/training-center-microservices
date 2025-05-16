package com.smalaca.discountmanagement.api;

import com.smalaca.contracts.metadata.CommandId;
import com.smalaca.contracts.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.contracts.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.contracts.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.discountmanagement.api.DiscountCodeAlreadyUsedEventAssertion.assertThatDiscountCodeAlreadyUsedEvent;
import static com.smalaca.discountmanagement.api.DiscountCodeUsedEventAssertion.assertThatDiscountCodeUsedEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.command.use-discount-code=" + DiscountCommandProcessorIntegrationTest.USE_DISCOUNT_CODE_COMMAND_TOPIC,
        "kafka.topics.event.discount-code-already-used=" + DiscountCommandProcessorIntegrationTest.DISCOUNT_CODE_ALREADY_USED_EVENT_TOPIC,
        "kafka.topics.event.discount-code-used=" + DiscountCommandProcessorIntegrationTest.DISCOUNT_CODE_USED_EVENT_TOPIC
})
@Import(DiscountManagementPivotalEventTestConsumer.class)
class DiscountCommandProcessorIntegrationTest {
    private static final Faker FAKER = new Faker();
    private static final BigDecimal ORIGINAL_PRICE = new BigDecimal("100.00");
    private static final BigDecimal FINAL_PRICE = new BigDecimal("90.00");

    protected static final String USE_DISCOUNT_CODE_COMMAND_TOPIC = "use-discount-code-command-topic";
    protected static final String DISCOUNT_CODE_ALREADY_USED_EVENT_TOPIC = "discount-code-already-used-event-topic";
    protected static final String DISCOUNT_CODE_USED_EVENT_TOPIC = "discount-code-used-event-topic";

    @Autowired
    private KafkaTemplate<String, Object> producerFactory;

    @Autowired
    private DiscountManagementPivotalEventTestConsumer consumer;

    @Test
    void shouldPublishDiscountCodeUsedEvent() {
        UseDiscountCodeCommand command = useDiscountCodeCommand();

        producerFactory.send(USE_DISCOUNT_CODE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<DiscountCodeUsedEvent> actual = consumer.discountCodeUsedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatDiscountCodeUsedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasParticipantId(command.participantId())
                    .hasTrainingId(command.trainingId())
                    .hasDiscountCode(command.discountCode())
                    .hasOriginalPrice(command.priceAmount())
                    .hasNewPrice(FINAL_PRICE)
                    .hasPriceCurrency(command.priceCurrencyCode());
        });
    }

    @Test
    void shouldPublishDiscountCodeAlreadyUsedEventWhenDiscountCodeAlreadyUsed() {
        String discountCode = randomDiscountCode();
        UUID offerId = randomId();
        producerFactory.send(USE_DISCOUNT_CODE_COMMAND_TOPIC, useDiscountCodeCommand(discountCode, offerId));
        UseDiscountCodeCommand command = useDiscountCodeCommand(discountCode, offerId);

        producerFactory.send(USE_DISCOUNT_CODE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<DiscountCodeAlreadyUsedEvent> actual = consumer.discountCodeAlreadyUsedEventFor(offerId);
            assertThat(actual).isPresent();

            assertThatDiscountCodeAlreadyUsedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(offerId)
                    .hasParticipantId(command.participantId())
                    .hasTrainingId(command.trainingId())
                    .hasDiscountCode(discountCode);
        });
    }

    private UseDiscountCodeCommand useDiscountCodeCommand() {
        return useDiscountCodeCommand(randomDiscountCode());
    }

    private UseDiscountCodeCommand useDiscountCodeCommand(String discountCode) {
        return useDiscountCodeCommand(discountCode, randomId());
    }

    private UseDiscountCodeCommand useDiscountCodeCommand(String discountCode, UUID offerId) {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        return new UseDiscountCodeCommand(
                commandId,
                offerId,
                randomId(),
                randomId(),
                ORIGINAL_PRICE,
                FAKER.currency().code(),
                discountCode
        );
    }

    private String randomDiscountCode() {
        return FAKER.commerce().promotionCode();
    }

    private static UUID randomId() {
        return UUID.randomUUID();
    }
}