package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import com.smalaca.opentrainings.query.order.OrderView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderPivotalEventPublisher {
    private final OrderQueryService orderQueryService;
    private final KafkaTemplate<String, TrainingPurchasedPivotalEvent> kafkaTemplate;
    private final String topicName;

    OrderPivotalEventPublisher(
            OrderQueryService orderQueryService,
            KafkaTemplate<String, TrainingPurchasedPivotalEvent> kafkaTemplate,
            @Value("${kafka.topics.order.pivotal.training-purchased}") String topicName) {
        this.orderQueryService = orderQueryService;
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @EventListener
    public void consume(TrainingPurchasedEvent event) {
        OrderView order = orderQueryService.findById(event.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found for ID: " + event.orderId()));

        TrainingPurchasedPivotalEvent pivotalEvent = new TrainingPurchasedPivotalEvent(event, order);

        kafkaTemplate.send(topicName, pivotalEvent);
    }
}
