package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import com.smalaca.opentrainings.query.order.OrderView;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

class OrderPivotalEventPublisher {
    private final OrderQueryService orderQueryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Topics topics;

    OrderPivotalEventPublisher(OrderQueryService orderQueryService, KafkaTemplate<String, Object> kafkaTemplate, Topics topics) {
        this.orderQueryService = orderQueryService;
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    @EventListener
    public void consume(TrainingPurchasedEvent event) {
        TrainingPurchasedPivotalEvent pivotalEvent = new TrainingPurchasedPivotalEvent(event, orderFor(event.orderId()));

        kafkaTemplate.send(topics.trainingPurchased(), pivotalEvent);
    }

    @EventListener
    public void consume(OrderRejectedEvent event) {
        OrderRejectedPivotalEvent pivotalEvent = new OrderRejectedPivotalEvent(event, orderFor(event.orderId()));

        kafkaTemplate.send(topics.orderRejected(), pivotalEvent);
    }

    @EventListener
    public void consume(OrderTerminatedEvent event) {
        OrderTerminatedPivotalEvent pivotalEvent = new OrderTerminatedPivotalEvent(event, orderFor(event.orderId()));

        kafkaTemplate.send(topics.orderTerminated(), pivotalEvent);
    }

    @EventListener
    public void consume(OrderCancelledEvent event) {
        OrderCancelledPivotalEvent pivotalEvent = new OrderCancelledPivotalEvent(event, orderFor(event.orderId()));

        kafkaTemplate.send(topics.orderCancelled(), pivotalEvent);
    }

    private OrderView orderFor(UUID orderId) {
        return orderQueryService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found for ID: " + orderId));
    }
}
