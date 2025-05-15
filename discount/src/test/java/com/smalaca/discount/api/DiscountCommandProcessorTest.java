package com.smalaca.discount.api;

import com.smalaca.contracts.metadata.CommandId;
import com.smalaca.contracts.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.contracts.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.contracts.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.discount.domain.DiscountCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiscountCommandProcessorTest {

    private static final String DISCOUNT_CODE_USED_TOPIC = "discount-code-used-topic";
    private static final String DISCOUNT_CODE_ALREADY_USED_TOPIC = "discount-code-already-used-topic";
    private static final String DISCOUNT_CODE = "DISCOUNT123";
    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID PARTICIPANT_ID = UUID.randomUUID();
    private static final UUID TRAINING_ID = UUID.randomUUID();
    private static final BigDecimal PRICE_AMOUNT = new BigDecimal("100.00");
    private static final String PRICE_CURRENCY_CODE = "USD";
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("0.10");
    private static final BigDecimal EXPECTED_NEW_PRICE = new BigDecimal("90.00");

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private DiscountCodeRepository discountCodeRepository;

    @InjectMocks
    private DiscountCommandProcessor processor;

    @Captor
    private ArgumentCaptor<DiscountCodeUsedEvent> discountCodeUsedEventCaptor;

    @Captor
    private ArgumentCaptor<DiscountCodeAlreadyUsedEvent> discountCodeAlreadyUsedEventCaptor;

    @Test
    void shouldPublishDiscountCodeUsedEventWhenDiscountCodeIsNotUsed() {
        // given
        UseDiscountCodeCommand command = createUseDiscountCodeCommand();
        given(discountCodeRepository.isAlreadyUsed(DISCOUNT_CODE)).willReturn(false);
        given(discountCodeRepository.getDiscountPercentage(DISCOUNT_CODE)).willReturn(DISCOUNT_PERCENTAGE);

        // when
        processor = new DiscountCommandProcessor(kafkaTemplate, DISCOUNT_CODE_USED_TOPIC, DISCOUNT_CODE_ALREADY_USED_TOPIC, discountCodeRepository);
        processor.process(command);

        // then
        verify(discountCodeRepository).markAsUsed(DISCOUNT_CODE);
        verify(kafkaTemplate).send(eq(DISCOUNT_CODE_USED_TOPIC), discountCodeUsedEventCaptor.capture());

        DiscountCodeUsedEvent event = discountCodeUsedEventCaptor.getValue();
        assertThat(event.offerId()).isEqualTo(OFFER_ID);
        assertThat(event.participantId()).isEqualTo(PARTICIPANT_ID);
        assertThat(event.trainingId()).isEqualTo(TRAINING_ID);
        assertThat(event.discountCode()).isEqualTo(DISCOUNT_CODE);
        assertThat(event.originalPrice()).isEqualTo(PRICE_AMOUNT);
        assertThat(event.newPrice().compareTo(EXPECTED_NEW_PRICE)).isZero();
        assertThat(event.priceCurrency()).isEqualTo(PRICE_CURRENCY_CODE);
    }

    @Test
    void shouldPublishDiscountCodeAlreadyUsedEventWhenDiscountCodeIsAlreadyUsed() {
        // given
        UseDiscountCodeCommand command = createUseDiscountCodeCommand();
        given(discountCodeRepository.isAlreadyUsed(DISCOUNT_CODE)).willReturn(true);

        // when
        processor = new DiscountCommandProcessor(kafkaTemplate, DISCOUNT_CODE_USED_TOPIC, DISCOUNT_CODE_ALREADY_USED_TOPIC, discountCodeRepository);
        processor.process(command);

        // then
        verify(kafkaTemplate).send(eq(DISCOUNT_CODE_ALREADY_USED_TOPIC), discountCodeAlreadyUsedEventCaptor.capture());

        DiscountCodeAlreadyUsedEvent event = discountCodeAlreadyUsedEventCaptor.getValue();
        assertThat(event.offerId()).isEqualTo(OFFER_ID);
        assertThat(event.participantId()).isEqualTo(PARTICIPANT_ID);
        assertThat(event.trainingId()).isEqualTo(TRAINING_ID);
        assertThat(event.discountCode()).isEqualTo(DISCOUNT_CODE);
    }

    private UseDiscountCodeCommand createUseDiscountCodeCommand() {
        CommandId commandId = new CommandId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        return new UseDiscountCodeCommand(commandId, OFFER_ID, PARTICIPANT_ID, TRAINING_ID, PRICE_AMOUNT, PRICE_CURRENCY_CODE, DISCOUNT_CODE);
    }
}
