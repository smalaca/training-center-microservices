package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import com.smalaca.opentrainings.query.order.OrderView;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

class OrderPivotalEventPublisher {
    private final OrderQueryService orderQueryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trainingPurchasedTopic;
    private final String orderRejectedTopic;

    OrderPivotalEventPublisher(OrderQueryService orderQueryService, KafkaTemplate<String, Object> kafkaTemplate, String trainingPurchasedTopic, String orderRejectedTopic) {
        this.orderQueryService = orderQueryService;
        this.kafkaTemplate = kafkaTemplate;
        this.trainingPurchasedTopic = trainingPurchasedTopic;
        this.orderRejectedTopic = orderRejectedTopic;
    }

    @EventListener
    public void consume(TrainingPurchasedEvent event) {
        TrainingPurchasedPivotalEvent pivotalEvent = new TrainingPurchasedPivotalEvent(event, orderFor(event.orderId()));

        kafkaTemplate.send(trainingPurchasedTopic, pivotalEvent);
    }

    @EventListener
    public void consume(OrderRejectedEvent event) {
        OrderRejectedPivotalEvent pivotalEvent = new OrderRejectedPivotalEvent(event, orderFor(event.orderId()));

        kafkaTemplate.send(orderRejectedTopic, pivotalEvent);
    }

    private OrderView orderFor(UUID orderId) {
        return orderQueryService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found for ID: " + orderId));
    }
}
