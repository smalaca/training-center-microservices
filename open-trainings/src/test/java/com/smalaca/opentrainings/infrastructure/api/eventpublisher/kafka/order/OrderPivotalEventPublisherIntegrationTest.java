package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.annotation.disable.DisabledSchedulerIntegrations;
import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.TrainingPurchasedPivotalEventTestConfiguration.TrainingPurchasedPivotalEventTestConsumer;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import com.smalaca.opentrainings.query.order.OrderView;
import com.smalaca.test.type.SpringBootIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Optional;

import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.OrderCancelledPivotalEventAssertion.assertThatOrderCancelledPivotalEvent;
import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.OrderRejectedPivotalEventAssertion.assertThatOrderRejectedPivotalEvent;
import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.OrderTerminatedPivotalEventAssertion.assertThatOrderTerminatedPivotalEvent;
import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.TrainingPurchasedPivotalEventAssertion.assertThatTrainingPurchasedPivotalEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Import(TrainingPurchasedPivotalEventTestConfiguration.class)
@DisabledSchedulerIntegrations
class OrderPivotalEventPublisherIntegrationTest {
    @Autowired
    private OrderPivotalEventPublisher publisher;

    @Autowired
    private TrainingPurchasedPivotalEventTestConsumer consumer;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderQueryService orderQueryService;

    private GivenOrderFactory given;

    @BeforeEach
    void init() {
        given = GivenOrderFactory.create(orderRepository);
    }

    @Test
    void shouldPublishTrainingPurchasedPivotalEvent() {
        OrderTestDto dto = given.order().confirmed().getDto();
        TrainingPurchasedEvent event = TrainingPurchasedEvent.create(dto.getOrderId(), dto.getOfferId(), dto.getTrainingId(), dto.getParticipantId());

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<TrainingPurchasedPivotalEvent> actual = consumer.trainingPurchasedPivotalEventFor(dto.getOrderId());
            assertThat(actual).isPresent();
            OrderView expected = orderQueryService.findById(dto.getOrderId()).get();

            assertThatTrainingPurchasedPivotalEvent(actual.get())
                .isNextAfter(event.eventId())
                .hasOrderId(expected.getOrderId())
                .hasOfferId(expected.getOfferId())
                .hasTrainingId(expected.getTrainingId())
                .hasParticipantId(expected.getParticipantId())
                .hasOrderNumber(expected.getOrderNumber())
                .hasTrainingPriceAmount(expected.getTrainingPriceAmount())
                .hasTrainingPriceCurrency(expected.getTrainingPriceCurrency())
                .hasFinalPriceAmount(expected.getFinalPriceAmount())
                .hasFinalPriceCurrency(expected.getFinalPriceCurrency())
                .hasOrderCreationDateTime(expected.getCreationDateTime())
                .hasDiscountCode(expected.getDiscountCode());
        });
    }

    @Test
    void shouldPublishOrderRejectedPivotalEvent() {
        OrderTestDto dto = given.order().rejected().getDto();
        OrderRejectedEvent event = OrderRejectedEvent.paymentFailed(dto.getOrderId());

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<OrderRejectedPivotalEvent> actual = consumer.orderRejectedPivotalEventFor(dto.getOrderId());
            assertThat(actual).isPresent();
            OrderView expected = orderQueryService.findById(dto.getOrderId()).get();

            assertThatOrderRejectedPivotalEvent(actual.get())
                .isNextAfter(event.eventId())
                .hasOrderId(expected.getOrderId())
                .hasReason(event.reason())
                .hasOfferId(expected.getOfferId())
                .hasTrainingId(expected.getTrainingId())
                .hasParticipantId(expected.getParticipantId())
                .hasOrderNumber(expected.getOrderNumber())
                .hasTrainingPriceAmount(expected.getTrainingPriceAmount())
                .hasTrainingPriceCurrency(expected.getTrainingPriceCurrency())
                .hasFinalPriceAmount(expected.getFinalPriceAmount())
                .hasFinalPriceCurrency(expected.getFinalPriceCurrency())
                .hasOrderCreationDateTime(expected.getCreationDateTime())
                .hasDiscountCode(expected.getDiscountCode());
        });
    }

    @Test
    void shouldPublishOrderTerminatedPivotalEvent() {
        OrderTestDto dto = given.order().rejected().getDto();
        OrderTerminatedEvent event = OrderTerminatedEvent.create(dto.getOrderId(), dto.getOfferId(), dto.getTrainingId(), dto.getParticipantId());

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<OrderTerminatedPivotalEvent> actual = consumer.orderTerminatedPivotalEventFor(dto.getOrderId());
            assertThat(actual).isPresent();
            OrderView expected = orderQueryService.findById(dto.getOrderId()).get();

            assertThatOrderTerminatedPivotalEvent(actual.get())
                .isNextAfter(event.eventId())
                .hasOrderId(expected.getOrderId())
                .hasOfferId(expected.getOfferId())
                .hasTrainingId(expected.getTrainingId())
                .hasParticipantId(expected.getParticipantId())
                .hasOrderNumber(expected.getOrderNumber())
                .hasTrainingPriceAmount(expected.getTrainingPriceAmount())
                .hasTrainingPriceCurrency(expected.getTrainingPriceCurrency())
                .hasFinalPriceAmount(expected.getFinalPriceAmount())
                .hasFinalPriceCurrency(expected.getFinalPriceCurrency())
                .hasOrderCreationDateTime(expected.getCreationDateTime())
                .hasDiscountCode(expected.getDiscountCode());
        });
    }

    @Test
    void shouldPublishOrderCancelledPivotalEvent() {
        OrderTestDto dto = given.order().rejected().getDto();
        OrderCancelledEvent event = OrderCancelledEvent.create(dto.getOrderId(), dto.getOfferId(), dto.getTrainingId(), dto.getParticipantId());

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<OrderCancelledPivotalEvent> actual = consumer.orderCancelledPivotalEventFor(dto.getOrderId());
            assertThat(actual).isPresent();
            OrderView expected = orderQueryService.findById(dto.getOrderId()).get();

            assertThatOrderCancelledPivotalEvent(actual.get())
                .isNextAfter(event.eventId())
                .hasOrderId(expected.getOrderId())
                .hasOfferId(expected.getOfferId())
                .hasTrainingId(expected.getTrainingId())
                .hasParticipantId(expected.getParticipantId())
                .hasOrderNumber(expected.getOrderNumber())
                .hasTrainingPriceAmount(expected.getTrainingPriceAmount())
                .hasTrainingPriceCurrency(expected.getTrainingPriceCurrency())
                .hasFinalPriceAmount(expected.getFinalPriceAmount())
                .hasFinalPriceCurrency(expected.getFinalPriceCurrency())
                .hasOrderCreationDateTime(expected.getCreationDateTime())
                .hasDiscountCode(expected.getDiscountCode());
        });
    }
}
