package com.smalaca.opentrainings.domain.order.commands;

import com.smalaca.opentrainings.domain.order.Price;

import java.util.UUID;

public record CreateOrderCommand(UUID trainingId, UUID participantId, Price price) {
}
